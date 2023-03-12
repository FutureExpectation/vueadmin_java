package com.markerhub.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.markerhub.common.dto.PassDto;
import com.markerhub.common.lang.Const;
import com.markerhub.common.lang.Result;
import com.markerhub.entity.SysRole;
import com.markerhub.entity.SysUser;
import com.markerhub.entity.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
@RequestMapping("/sys/user")
public class SysUserController extends BaseController {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/info/{id}")
    public Result info(@PathVariable Long id){
        SysUser sysUser = sysUserService.getById(id);

        Assert.notNull(sysUser,"找不到该管理员");

        List<SysRole> roles =sysRoleService.listRolesByUserId(id);

        sysUser.setSysRoles(roles);

        List<Long> roleIds = roles.stream().map(p -> (p.getId())).collect(Collectors.toList());

        sysUser.setIds(roleIds);
        return Result.succ(sysUser);

    }

    @PostMapping("/list")
    public Result list(String username){
        System.out.println("username----------》"+username);
        Page<SysUser> pageData = sysUserService.page(getPage()
                , new QueryWrapper<SysUser>().like(StrUtil.isNotBlank(username), "username", username));

        pageData.getRecords().forEach(u -> {
            u.setSysRoles(sysRoleService.listRolesByUserId(u.getId()));
        });

        return Result.succ(pageData);

    }

    @PostMapping("/save")
    public Result save(@Validated @RequestBody SysUser sysUser){
        sysUser.setCreated(LocalDateTime.now());
        sysUser.setStatu(Const.STATUS_ON);

        String password = bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD);
        sysUser.setPassword(password);

        sysUser.setAvatar(Const.DEFAULT_AVATAR);
        sysUserService.save(sysUser);


        return Result.succ(sysUser);

    }


    @PostMapping("/update")
    public Result update(@Validated @RequestBody SysUser sysUser){
        sysUser.setCreated(LocalDateTime.now());
        sysUserService.updateById(sysUser);

        return Result.succ(sysUser);

    }
    /**
     * @description:
     * @param: ids-> user_id[]
     * @return: com.markerhub.common.lang.Result
     * @author: Lianggl
     * @date: 2023/3/8 10:21
     */
    @PostMapping("/delete")
    @Transactional
    public Result delete(@RequestBody Long[] ids){

        sysUserService.removeByIds(Arrays.asList(ids));

        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().in("user_id",ids));

        return Result.succ("");

    }
    @Transactional
    @PostMapping("/role/{userId}")
    public Result rolePerm(@PathVariable("userId") Long userId, @RequestBody Long[] roleIds){
        System.out.println("roleIds"+roleIds);
        List<SysUserRole> userRoles = new ArrayList<>();
        Arrays.stream(roleIds).forEach(r->{
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setRoleId(r);
            sysUserRole.setUserId(userId);

            userRoles.add(sysUserRole);
        });
        sysUserRoleService.remove(new QueryWrapper<SysUserRole>().eq("user_id",userId));
        sysUserRoleService.saveBatch(userRoles);

        //删除缓存
        SysUser sysUser = sysUserService.getById(userId);
        sysUserService.clearUserAuthorityInfo(sysUser.getUsername());

        return Result.succ("");

    }

    @PostMapping("/repass/{userId}")
    public Result repass(@PathVariable Long userId){

        SysUser sysUser = sysUserService.getById(userId);

        sysUser.setPassword(bCryptPasswordEncoder.encode(Const.DEFAULT_PASSWORD));
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);

        return Result.succ("");

    }


    @PostMapping("/updatePass")
    public Result updatePass(@Validated @RequestBody PassDto passDto, Principal principal){

        SysUser sysUser = sysUserService.getByUsername(principal.getName());
        boolean macths = bCryptPasswordEncoder.matches(passDto.getCurrentPass(),sysUser.getPassword());

        if(!macths){
            return Result.fail("旧密码不正确！");
        }

        sysUser.setPassword(bCryptPasswordEncoder.encode(passDto.getCurrentPass()));
        sysUser.setUpdated(LocalDateTime.now());
        sysUserService.updateById(sysUser);

        return Result.succ("");

    }
}
