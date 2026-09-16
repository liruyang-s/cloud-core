package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.common.core.exception.ServiceException;
import com.micro.cloud.common.core.utils.SecurityUtils;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.common.mybatis.core.LoginUserHolder;
import com.micro.cloud.common.redis.utils.CacheUtils;
import com.micro.cloud.user.domain.SysRole;
import com.micro.cloud.user.domain.SysUser;
import com.micro.cloud.user.domain.SysUserPost;
import com.micro.cloud.user.domain.SysUserRole;
import com.micro.cloud.user.mapper.SysDeptMapper;
import com.micro.cloud.user.mapper.SysMenuMapper;
import com.micro.cloud.user.mapper.SysRoleMapper;
import com.micro.cloud.user.mapper.SysUserMapper;
import com.micro.cloud.user.mapper.SysUserPostMapper;
import com.micro.cloud.user.mapper.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    private final SysUserRoleMapper userRoleMapper;
    private final SysUserPostMapper userPostMapper;
    private final SysRoleMapper roleMapper;
    private final SysMenuMapper menuMapper;
    private final SysDeptMapper deptMapper;
    private final CacheUtils cacheUtils;

    private static final String USER_INFO_CACHE_KEY = "user:info:";
    private static final long USER_INFO_TTL = 1800;

    /**
     * 用户列表（数据权限过滤）
     */
    public List<SysUser> selectUserList(String userName, String phonenumber, String status, Long deptId) {
        return baseMapper.selectUserList(userName, phonenumber, status, deptId);
    }

    /**
     * 新增用户（含角色、岗位关联）
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertUser(SysUser user) {
        checkUserNameUnique(user.getUserName());
        checkPasswordStrength(user.getUserName(), user.getPassword());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        save(user);
        insertUserRole(user.getUserId(), user.getRoleIds());
        insertUserPost(user.getUserId(), user.getPostIds());
    }

    /**
     * 修改用户（含角色、岗位关联）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(SysUser user) {
        // 先取旧记录：账号可能被修改，需按旧账号失效缓存
        SysUser oldUser = getById(user.getUserId());
        if (oldUser == null) {
            throw new ServiceException("用户不存在或已被删除");
        }
        user.setPassword(null);
        updateById(user);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getUserId()));
        insertUserRole(user.getUserId(), user.getRoleIds());
        userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>()
                .eq(SysUserPost::getUserId, user.getUserId()));
        insertUserPost(user.getUserId(), user.getPostIds());
        cacheUtils.remove(USER_INFO_CACHE_KEY + oldUser.getUserName());
        if (user.getUserName() != null && !user.getUserName().equals(oldUser.getUserName())) {
            cacheUtils.remove(USER_INFO_CACHE_KEY + user.getUserName());
        }
    }

    /**
     * 删除用户（逻辑删除 + 清理关联）
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserByIds(List<Long> userIds) {
        LoginUser current = LoginUserHolder.get();
        for (Long userId : userIds) {
            // 删除保护：不允许删除自己
            if (current != null && userId.equals(current.getUserId())) {
                throw new ServiceException("不允许删除当前登录用户");
            }
            SysUser user = getById(userId);
            if (user != null) {
                // 删除保护：不允许删除超级管理员
                if ("admin".equals(user.getUserName())) {
                    throw new ServiceException("不允许删除超级管理员账户");
                }
                removeById(userId);
                userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                        .eq(SysUserRole::getUserId, userId));
                userPostMapper.delete(new LambdaQueryWrapper<SysUserPost>()
                        .eq(SysUserPost::getUserId, userId));
                cacheUtils.remove(USER_INFO_CACHE_KEY + user.getUserName());
            }
        }
    }

    /**
     * 重置密码
     */
    public void resetPassword(Long userId, String password) {
        if (password == null || password.isBlank()) {
            throw new ServiceException("新密码不能为空");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new ServiceException("密码长度必须在 6 到 20 个字符之间");
        }
        SysUser user = getById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在或已被删除");
        }
        checkPasswordStrength(user.getUserName(), password);
        SysUser update = new SysUser();
        update.setUserId(userId);
        update.setPassword(SecurityUtils.encryptPassword(password));
        updateById(update);
        cacheUtils.remove(USER_INFO_CACHE_KEY + user.getUserName());
    }

    /**
     * 获取登录用户完整信息（认证中心调用，带缓存）
     */
    public LoginUser getLoginUser(String userName) {
        return cacheUtils.get(USER_INFO_CACHE_KEY + userName, USER_INFO_TTL, () -> loadLoginUser(userName));
    }

    private LoginUser loadLoginUser(String userName) {
        SysUser user = baseMapper.selectByUserName(userName);
        if (user == null) {
            return null;
        }
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setNickName(user.getNickName());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setPassword(user.getPassword());
        loginUser.setStatus(user.getStatus());

        // 角色与权限
        List<SysRole> roles = roleMapper.selectRolesByUserId(user.getUserId());
        Set<String> roleKeys = new HashSet<>();
        boolean admin = false;
        String dataScope = "5";
        Set<Long> dataScopeDeptIds = new HashSet<>();
        for (SysRole role : roles) {
            roleKeys.add(role.getRoleKey());
            if ("admin".equals(role.getRoleKey())) {
                admin = true;
            }
            // 数据范围取最大权限（数字越小范围越大）
            if (role.getDataScope() != null && role.getDataScope().compareTo(dataScope) < 0) {
                dataScope = role.getDataScope();
            }
            if ("2".equals(role.getDataScope())) {
                dataScopeDeptIds.addAll(deptMapper.selectDeptIdsByRoleId(role.getRoleId()));
            }
        }
        loginUser.setRoleKeys(roleKeys);
        loginUser.setAdmin(admin);
        loginUser.setDataScope(admin ? "1" : dataScope);
        loginUser.setDataScopeDeptIds(dataScopeDeptIds);
        loginUser.setPermissions(menuMapper.selectPermsByUserId(user.getUserId()));
        return loginUser;
    }

    private void checkUserNameUnique(String userName) {
        if (baseMapper.selectByUserName(userName) != null) {
            throw new ServiceException("用户账号已存在: " + userName);
        }
    }

    /** 常见弱口令黑名单 */
    private static final List<String> WEAK_PASSWORDS = List.of(
            "123456", "12345678", "123456789", "111111", "000000",
            "666666", "888888", "654321", "qwerty", "password",
            "admin123", "admin888", "root123", "abc123", "123abc");

    /**
     * 弱口令校验：长度、复杂度、黑名单、与账号相关性
     */
    private void checkPasswordStrength(String userName, String password) {
        if (password == null || password.isBlank()) {
            throw new ServiceException("密码不能为空");
        }
        if (password.length() < 6 || password.length() > 20) {
            throw new ServiceException("密码长度必须在 6 到 20 个字符之间");
        }
        if (WEAK_PASSWORDS.contains(password.toLowerCase())) {
            throw new ServiceException("密码过于简单，请使用更复杂的密码");
        }
        if (userName != null && !userName.isBlank()
                && password.toLowerCase().contains(userName.toLowerCase())) {
            throw new ServiceException("密码中不能包含用户账号");
        }
        int complexity = 0;
        if (password.chars().anyMatch(Character::isUpperCase)) {
            complexity++;
        }
        if (password.chars().anyMatch(Character::isLowerCase)) {
            complexity++;
        }
        if (password.chars().anyMatch(Character::isDigit)) {
            complexity++;
        }
        if (password.chars().anyMatch(c -> !Character.isLetterOrDigit(c))) {
            complexity++;
        }
        if (complexity < 2) {
            throw new ServiceException("密码必须包含大小写字母、数字、特殊字符中的至少两类");
        }
    }

    private void insertUserRole(Long userId, List<Long> roleIds) {
        if (roleIds != null) {
            for (Long roleId : roleIds) {
                userRoleMapper.insert(new SysUserRole(userId, roleId));
            }
        }
    }

    private void insertUserPost(Long userId, List<Long> postIds) {
        if (postIds != null) {
            for (Long postId : postIds) {
                userPostMapper.insert(new SysUserPost(userId, postId));
            }
        }
    }
}
