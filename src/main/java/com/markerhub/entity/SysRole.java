package com.markerhub.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

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
public class SysRole extends BaseEntity {

    private static final long serialVersionUID = 1L;
    @NotBlank(message = "角色名称不能为为空")
    private String name;
    @NotBlank(message = "角色编码不能为为空")
    private String code;

    /**
     * 备注
     */
    private String remark;
    private LocalDateTime created;
    /**
     * 修改时间
     */
    private LocalDateTime updated;

    private Integer statu;

    @TableField(exist = false)
    private List<Long> menuIds = new ArrayList<>();
}
