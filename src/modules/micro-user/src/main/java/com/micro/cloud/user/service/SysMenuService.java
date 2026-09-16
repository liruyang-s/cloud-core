package com.micro.cloud.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.micro.cloud.common.mybatis.core.LoginUser;
import com.micro.cloud.common.mybatis.core.LoginUserHolder;
import com.micro.cloud.user.domain.SysMenu;
import com.micro.cloud.user.mapper.SysMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单服务
 */
@Service
@RequiredArgsConstructor
public class SysMenuService extends ServiceImpl<SysMenuMapper, SysMenu> {

    /** 菜单列表 */
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(menu.getMenuName() != null, SysMenu::getMenuName, menu.getMenuName())
                .eq(menu.getVisible() != null, SysMenu::getVisible, menu.getVisible())
                .eq(menu.getStatus() != null, SysMenu::getStatus, menu.getStatus())
                .orderByAsc(SysMenu::getParentId, SysMenu::getOrderNum);
        return list(wrapper);
    }

    /** 构建菜单树 */
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> roots = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                roots.add(menu);
            } else {
                menus.stream()
                        .filter(m -> m.getMenuId().equals(menu.getParentId()))
                        .findFirst()
                        .ifPresentOrElse(parent -> parent.getChildren().add(menu),
                                () -> roots.add(menu));
            }
        }
        return roots;
    }

    /** 当前登录用户的路由菜单 */
    public List<SysMenu> selectMenusByUserId(Long userId) {
        return buildMenuTree(baseMapper.selectMenusByUserId(userId));
    }
}
