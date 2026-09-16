package com.micro.cloud.common.mybatis.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.micro.cloud.common.core.constant.Constants;
import com.micro.cloud.common.mybatis.annotation.DataScope;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.common.mybatis.core.LoginUserHolder;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 数据权限拦截器
 * <p>
 * 拦截标注 {@link DataScope} 注解的 Mapper 方法，根据当前登录用户的数据范围
 * 动态拼接 SQL 过滤条件：
 * <ul>
 *   <li>1 全部数据权限：不拼接条件</li>
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

    /** Mapper 方法注解缓存 */
    private final Map<String, DataScope> dataScopeCache = new ConcurrentHashMap<>();

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
        if (StrUtil.isNotBlank(sqlSegment)) {
            String newSql = "SELECT * FROM (" + boundSql.getSql() + ") temp_data_scope WHERE " + sqlSegment;
            try {
                java.lang.reflect.Field sqlField = BoundSql.class.getDeclaredField("sql");
                sqlField.setAccessible(true);
                sqlField.set(boundSql, newSql);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new SQLException("数据权限 SQL 改写失败", e);
            }
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
            try {
                String className = id.substring(0, id.lastIndexOf("."));
                String methodName = id.substring(id.lastIndexOf(".") + 1);
                Class<?> clazz = Class.forName(className);
                for (Method method : clazz.getMethods()) {
                    if (method.getName().equals(methodName)) {
                        DataScope annotation = method.getAnnotation(DataScope.class);
                        if (annotation != null) {
                            return annotation;
                        }
                    }
                }
            } catch (ClassNotFoundException ignored) {
                // 非 Mapper 接口调用，忽略
            }
            return null;
        });
    }
}
