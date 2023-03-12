package com.markerhub.controller;


import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.common.dto.SysMenuDto;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysMenu;
import com.markerhub.entity.SysRoleMenu;
import com.markerhub.entity.SysUser;
import com.markerhub.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jobob
 * @since 2023-02-27
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController extends BaseController {
    @Autowired
    SysMenuService sysMenuService;

    /**
     * 用户当前用户的菜单和权限信息
     * */
    @GetMapping("/nav")
    public Object getMenu(Principal principal){

        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        //获取权限信息
        String authorityInfo = sysUserService.getUserAuthorityInfo(sysUser.getId());//ROLE_admin,sys:user:list,....

        String[] authorityInfoArray = StringUtils.tokenizeToStringArray(authorityInfo, ",");

        //获取导航栏信息
        List<SysMenuDto> navs =sysMenuService.getCurrentUserNav();

        return Result.succ(MapUtil.builder().put("authority",authorityInfoArray)
        .put("nav",navs).map());
    }
    @GetMapping("/info/{id}")
    @PreAuthorize("hasAuthority('sys:menu:list')")//权限配置
    public Result Info(@PathVariable(name = "id") Long id){
        return Result.succ(sysMenuService.getById(id));
    }


    @GetMapping("/list")
//    @PreAuthorize("hasAuthority('sys:menu:list')")//权限配置
    public Result list(){
        List<SysMenu> sysMenus =sysMenuService.tree();
        return Result.succ(sysMenus);
    }
    //菜单新增
    @PostMapping("/save")
    @PreAuthorize("hasAuthority('sys:menu:save')")//权限配置
    public Result save(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.save(sysMenu);

        return Result.succ(sysMenu);
    }
    //菜单修改
    @PostMapping("/update")
    @PreAuthorize("hasAuthority('sys:menu:update')")//权限配置
    public Result update(@Validated @RequestBody SysMenu sysMenu){
        sysMenu.setCreated(LocalDateTime.now());
        sysMenuService.updateById(sysMenu);

        sysUserService.clearUserAuthorityByMenuId(sysMenu.getId());
        return Result.succ(sysMenu);
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('sys:menu:delete')") //权限配置
    public Result delete(@PathVariable("id") Long id){
        int count = sysMenuService.count(new QueryWrapper<SysMenu>().eq("parent_id", id));
        if(count>0){
            return Result.fail("存在子菜单，请先删除子菜单！");
        }
        //清除缓存数据
        sysUserService.clearUserAuthorityByMenuId(id);

        sysMenuService.removeById(id);
        //同步删除中间关联表
        sysRoleMenuService.remove(new QueryWrapper<SysRoleMenu>().eq("menu_id",id));

        return Result.succ("删除成功");

    }
}
