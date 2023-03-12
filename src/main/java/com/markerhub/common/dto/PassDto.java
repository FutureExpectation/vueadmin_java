package com.markerhub.common.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Lianggl
 * @version 1.0
 * @description: TODO
 * @date 2023/3/8 17:28
 */
@Data
public class PassDto implements Serializable {
    @NotBlank(message = "旧密码不能为空")
    private String password;
    @NotBlank(message = "新密码不能为空")
    private String currentPass;
}
