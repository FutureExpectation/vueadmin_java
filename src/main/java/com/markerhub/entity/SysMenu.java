package com.markerhub.entity;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2023-02-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 父菜单ID，一级菜单为0
     */
    @NotNull(message = "上级菜单不能为空")
    private Long parentId;
    @NotBlank(message = "菜单不能为空")
    private String name;

    /**
     * 菜单URL
     */
    private String path;

    /**
     * 授权（多个用逗号分离，如：user:list,user:create）
     */
    @NotBlank(message = "授权码不能为空")
    private String perms;

    /**
     * 组件
     */
    private String component;

    /**
     * 类型 0：目录   1：菜单  2：按钮
     */
    @NotNull(message = "类型不能为空")
    private Integer type;

    /**
     * 菜单图标
     */
    private String icon;

    /**
     * 排序
     */
    @TableField("orderNum")
    private Integer orderNum;
    private LocalDateTime created;
    /**
     * 修改时间
     */
    private LocalDateTime updated;

    private Integer statu;
    @TableField(exist = false)
    private List<SysMenu> children =new ArrayList<>();
}
