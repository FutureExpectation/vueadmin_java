package com.markerhub.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.markerhub.common.dto.SysMenuDto;
import com.markerhub.entity.SysMenu;
import com.markerhub.entity.SysUser;
import com.markerhub.mapper.SysMenuMapper;
import com.markerhub.mapper.SysUserMapper;
import com.markerhub.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.markerhub.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jobob
 * @since 2023-02-27
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysUserMapper sysUserMapper;
    @Override
    public List<SysMenuDto> getCurrentUserNav() {
        String  username =(String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        SysUser sysUser = sysUserService.getByUsername(username);

        List<Long> navMenuIds = sysUserMapper.getNavMenuIds(sysUser.getId());
        List<SysMenu> sysMenus = this.listByIds(navMenuIds);

        //转树状结构
        List<SysMenu> menuTree = buildTreeMenu(sysMenus);

        //实体转Dto
        return convert(menuTree);

    }

    @Override
    public List<SysMenu> tree() {
        //获取所有菜单信息
        List<SysMenu> sysMenus = this.list(new QueryWrapper<SysMenu>().orderByAsc("orderNum"));
        //转成树状结构
        return buildTreeMenu(sysMenus);

    }

    private List<SysMenuDto> convert(List<SysMenu> menuTree) {
        List<SysMenuDto> menuDtos =new ArrayList<>();
        menuTree.forEach(m->{
            SysMenuDto dto = new SysMenuDto();
            dto.setId(m.getId());
            dto.setIcon(m.getIcon());
            dto.setName(m.getPerms());
            dto.setTitle(m.getName());
            dto.setComponent(m.getComponent());
            dto.setPath(m.getPath());
            if (m.getChildren().size()>0){
                //子节点调用当前方法进行再次转换  递归
                dto.setChildren(convert(m.getChildren()));
            }
            menuDtos.add(dto);
        });
        return menuDtos;
    };

    private List<SysMenu> buildTreeMenu(List<SysMenu> sysMenus) {
        List<SysMenu> finalMenus =new ArrayList<>();

        //先各自寻找各自的孩子
        for(SysMenu menu:sysMenus){

            for (SysMenu e:sysMenus){
                if (menu.getId()==e.getParentId()){
                    menu.getChildren().add(e);
                }
            }

            //提取出父节点
            if (menu.getParentId()==0L){
                finalMenus.add(menu);
            }
        }

        System.out.println(JSONUtil.toJsonStr(finalMenus));

        return finalMenus;
    };
}
