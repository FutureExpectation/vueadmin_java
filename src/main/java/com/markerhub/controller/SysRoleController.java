package com.markerhub.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.lang.Const;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysRole;
import com.markerhub.entity.SysRoleMenu;
import com.markerhub.entity.SysUserRole;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2023-02-27
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController extends BaseController {
    /**
     * @description:
     * @param: id
     * @return: com.markerhub.common.lang.Result
     * @author: Lianggl
     * @date: 2023/3/7 15:18
     */


    @GetMapping("/info/{id}")
//    @PreAuthorize("hasAuthority('sys:role:info')")
    public Result info(@PathVariable(name = "id")Long id){

        SysRole sysRole = sysRoleService.getById(id);
        //获取角色相关连的菜单id
        List<SysRoleMenu> roleMenus = sysRoleMenuService.list(new QueryWrapper<SysRoleMenu>().eq("role_id", id));
        System.out.println("1111"+roleMenus);
        List<Long> menuIds = roleMenus.stream().map(p -> (p.getMenuId())).collect(Collectors.toList());

        sysRole.setMenuIds(menuIds);
        return Result.succ(sysRole);

    }

    @PostMapping("/list")
//    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result list(String name){
        System.out.println("name----------》"+name);
        Page<SysRole> pageData =sysRoleService.page(getPage(),
                new QueryWrapper<SysRole>().like(StrUtil.isNotBlank(name),"name",name));

        return Result.succ(pageData);
    }

    @PostMapping("/save")
//    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result save(@Validated @RequestBody SysRole sysRole){
        sysRole.setCreated(LocalDateTime.now());
        sysRole.setStatu(Const.STATUS_ON);
        sysRoleService.save(sysRole);
        return Result.succ(sysRole);
    }

    @PostMapping("/update")
//    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result update(@Validated @RequestBody SysRole sysRole){
        sysRole.setCreated(LocalDateTime.now());
        sysRoleService.updateById(sysRole);
        return Result.succ(sysRole);
    }

    @PostMapping("/delete")
//    @PreAuthorize("hasAuthority('sys:role:delete')")
    @Transactional
    public Result delete(@RequestBody Long[] ids){

        sysRoleService.removeByIds(Arrays.asList(ids));

        //删除中间表
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("role_id",ids));
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().in("role_id",ids));

        //删除缓存
        Arrays.stream(ids).forEach(id->{
            sysUserService.clearUserAuthorityByRoleId(id);
        });

        return Result.succ("");
    }
    //分配权限
    @PostMapping("/perm/{roleId}")
//    @PreAuthorize("hasAuthority('sys:role:perm')")
    @Transactional
    public Result perm(@PathVariable("roleId") Long roleId ,@RequestBody Long[] menuIds){

        List<SysRoleMenu> sysRoleMenus = new ArrayList<>();
        Arrays.stream(menuIds).forEach(menuId ->{
            SysRoleMenu roleMenu = new SysRoleMenu();
            roleMenu.setMenuId(menuId);
            roleMenu.setRoleId(roleId);

            sysRoleMenus.add(roleMenu);
        });

        //先删除原来的记录，再保存新的
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("role_id",roleId));
        sysRoleMenuService.saveBatch(sysRoleMenus);

        //删除缓存
        sysUserService.clearUserAuthorityByRoleId(roleId);

        return Result.succ(menuIds);
    }
}
