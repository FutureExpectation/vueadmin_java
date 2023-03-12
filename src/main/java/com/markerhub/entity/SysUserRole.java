package com.markerhub.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2023-02-27
 */
@Data
//@EqualsAndHashCode(callSuper = true)
public class SysUserRole{

    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type= IdType.AUTO)
    private Long id;
    private Long userId;

    private Long roleId;


}
