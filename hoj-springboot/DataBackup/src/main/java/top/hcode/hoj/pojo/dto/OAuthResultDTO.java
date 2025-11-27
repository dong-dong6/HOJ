package top.hcode.hoj.pojo.dto;

import lombok.Data;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: OAuth 登录结果 DTO
 */
@Data
public class OAuthResultDTO {

    /**
     * 是否需要绑定账号
     */
    private Boolean needBind;

    /**
     * 绑定临时凭证（用于后续绑定操作）
     */
    private String bindToken;

    /**
     * 平台类型
     */
    private String platform;

    /**
     * 第三方用户信息
     */
    private String openId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;

    /**
     * 如果已绑定账号，返回登录token
     */
    private String token;

    /**
     * 用户信息（登录成功时返回）
     */
    private Object userInfo;
}
