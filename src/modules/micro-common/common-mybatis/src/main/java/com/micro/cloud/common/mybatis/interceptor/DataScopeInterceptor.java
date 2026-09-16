package com.micro.cloud.common.mybatis.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.micro.cloud.common.mybatis.annotation.DataScope;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.common.mybatis.core.LoginUserHolder;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据权限拦截器
 * <p>
 * 拦截标注 {@link DataScope} 注解的 Mapper 方法，根据当前登录用户的数据范围
 * 动态向原查询 WHERE 子句追加过滤条件（别名与原 SQL 保持一致）：
 * <ul>
 *   <li>1 全部数据权限：不追加条件</li>
 *   <li>2 自定义数据权限：按角色关联的部门集合过滤</li>
 *   <li>3 本部门数据权限：仅本部门</li>
 *   <li>4 本部门及以下数据权限：本部门及子部门（ancestors 匹配）</li>
 *   <li>5 仅本人数据权限：按用户ID过滤</li>
 * </ul>
 */
public class DataScopeInterceptor implements InnerInterceptor {

    /** 数据范围常量 */
    private static final String DATA_SCOPE_ALL = "1";
    private static final String DATA_SCOPE_CUSTOM = "2";
    private static final String DATA_SCOPE_DEPT = "3";
    private static final String DATA_SCOPE_DEPT_AND_CHILD = "4";
    private static final String DATA_SCOPE_SELF = "5";

    /** Mapper 方法注解缓存（无注解的方法缓存 Optional.empty()，避免重复反射） */
    private final Map<String, Optional<DataScope>> dataScopeCache = new ConcurrentHashMap<>();

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter,
                            RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
        DataScope dataScope = getDataScope(ms);
        if (dataScope == null) {
            return;
        }
        LoginUser loginUser = LoginUserHolder.get();
        if (loginUser == null || loginUser.isAdmin()) {
            // 未登录或超级管理员不做过滤
            return;
        }
        String sqlSegment = buildSqlSegment(loginUser, dataScope);
        if (StrUtil.isBlank(sqlSegment)) {
            return;
        }
        String newSql = appendWhere(boundSql.getSql(), sqlSegment);
        try {
            java.lang.reflect.Field sqlField = BoundSql.class.getDeclaredField("sql");
            sqlField.setAccessible(true);
            sqlField.set(boundSql, newSql);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new SQLException("数据权限 SQL 改写失败", e);
        }
    }

    /**
     * 将数据权限条件追加到原查询的 WHERE 子句。
     * <p>
     * 不能将原 SQL 包裹为子查询后在外层过滤：内层表别名（如 u、d）在子查询外不可见，
     * 会导致 SQL 执行报错。此处通过 JSqlParser 解析原 SQL，将条件并入原 WHERE。
     */
    private String appendWhere(String originalSql, String sqlSegment) throws SQLException {
        try {
            Statement statement = CCJSqlParserUtil.parse(originalSql);
            if (!(statement instanceof Select select)) {
                return originalSql;
            }
            SelectBody selectBody = select.getSelectBody();
            if (!(selectBody instanceof PlainSelect plainSelect)) {
                // UNION 等复杂结构不做改写（数据权限查询应为简单 SELECT）
                return originalSql;
            }
            Expression condition = CCJSqlParserUtil.parseCondExpression(sqlSegment);
            Expression where = plainSelect.getWhere();
            plainSelect.setWhere(where == null ? condition : new AndExpression(where, condition));
            return select.toString();
        } catch (Exception e) {
            throw new SQLException("数据权限 SQL 解析失败: " + sqlSegment, e);
        }
    }

    /**
     * 根据数据范围构建过滤条件
     */
    private String buildSqlSegment(LoginUser loginUser, DataScope dataScope) {
        String deptAlias = dataScope.deptAlias();
        String userAlias = dataScope.userAlias();
        String scope = loginUser.getDataScope();
        if (StrUtil.isBlank(scope) || DATA_SCOPE_ALL.equals(scope)) {
            return null;
        }
        switch (scope) {
            case DATA_SCOPE_CUSTOM -> {
                if (CollUtil.isEmpty(loginUser.getDataScopeDeptIds())) {
                    return "1 = 0";
                }
                String deptIds = loginUser.getDataScopeDeptIds().stream()
                        .map(String::valueOf).collect(Collectors.joining(","));
                return "(" + deptAlias + ".dept_id IN (" + deptIds + "))";
            }
            case DATA_SCOPE_DEPT -> {
                return "(" + deptAlias + ".dept_id = " + loginUser.getDeptId() + ")";
            }
            case DATA_SCOPE_DEPT_AND_CHILD -> {
                return "(" + deptAlias + ".dept_id = " + loginUser.getDeptId()
                        + " OR " + deptAlias + ".dept_id IN (SELECT dept_id FROM sys_dept WHERE find_in_set("
                        + loginUser.getDeptId() + ", ancestors)))";
            }
            case DATA_SCOPE_SELF -> {
                return "(" + userAlias + ".user_id = " + loginUser.getUserId() + ")";
            }
            default -> {
                return null;
            }
        }
    }

    /**
     * 解析 Mapper 方法上的 @DataScope 注解（带缓存）
     */
    private DataScope getDataScope(MappedStatement ms) {
        return dataScopeCache.computeIfAbsent(ms.getId(), id -> {
            int lastDot = id.lastIndexOf(".");
            if (lastDot < 0) {
                return Optional.empty();
            }
            String className = id.substring(0, lastDot);
            String methodName = id.substring(lastDot + 1);
            try {
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(methodName)) {
                        DataScope annotation = method.getAnnotation(DataScope.class);
                        if (annotation != null) {
                            return Optional.of(annotation);
                        }
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // 非 Mapper 接口调用，忽略
            }
            return Optional.empty();
        }).orElse(null);
    }
}
