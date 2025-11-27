package top.hcode.hoj.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: OAuth 绑定请求 DTO
 */
@Data
public class OAuthBindDTO {

    /**
     * 绑定临时凭证
     */
    @NotBlank(message = "绑定凭证不能为空")
    private String bindToken;

    /**
     * 绑定方式：register-注册新账号, bindExist-绑定已有账号
     */
    @NotBlank(message = "绑定方式不能为空")
    private String bindType;

    /**
     * 用户名（注册新账号时必填）
     */
    private String username;

    /**
     * 密码（注册新账号或绑定已有账号时必填）
     */
    private String password;

    /**
     * 邮箱（注册新账号时可选）
     */
    private String email;
}
