package top.hcode.hoj.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: OAuth2 第三方登录配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "hoj.oauth")
public class OAuthConfig {

    /**
     * 是否开启第三方登录
     */
    private Boolean enabled = false;

    /**
     * GitHub OAuth配置
     */
    private OAuthClientConfig github;

    /**
     * Gitee OAuth配置
     */
    private OAuthClientConfig gitee;

    /**
     * GitLab OAuth配置
     */
    private OAuthClientConfig gitlab;

    /**
     * 自定义OAuth配置列表
     */
    private Map<String, CustomOAuthConfig> customs;

    @Data
    public static class OAuthClientConfig {
        /**
         * 是否启用
         */
        private Boolean enabled = false;

        /**
         * Client ID
         */
        private String clientId;

        /**
         * Client Secret
         */
        private String clientSecret;

        /**
         * 回调地址
         */
        private String redirectUri;
    }

    @Data
    public static class CustomOAuthConfig extends OAuthClientConfig {
        /**
         * 自定义OAuth名称（显示名称）
         */
        private String name;

        /**
         * 授权地址
         */
        private String authorizeUrl;

        /**
         * 获取Token地址
         */
        private String accessTokenUrl;

        /**
         * 获取用户信息地址
         */
        private String userInfoUrl;

        /**
         * 授权范围
         */
        private String scope;

        /**
         * 用户ID字段名
         */
        private String userIdField = "id";

        /**
         * 用户名字段名
         */
        private String usernameField = "username";

        /**
         * 昵称字段名
         */
        private String nicknameField = "name";

        /**
         * 头像字段名
         */
        private String avatarField = "avatar";

        /**
         * 邮箱字段名
         */
        private String emailField = "email";
    }
}
