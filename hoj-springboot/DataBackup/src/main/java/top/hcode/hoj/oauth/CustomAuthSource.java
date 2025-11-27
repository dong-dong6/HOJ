package top.hcode.hoj.oauth;

import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.request.AuthDefaultRequest;
import top.hcode.hoj.config.OAuthConfig;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: 自定义 OAuth 认证源
 */
public class CustomAuthSource implements AuthSource {

    private final String name;
    private final OAuthConfig.CustomOAuthConfig config;

    public CustomAuthSource(String name, OAuthConfig.CustomOAuthConfig config) {
        this.name = name;
        this.config = config;
    }

    @Override
    public String authorize() {
        return config.getAuthorizeUrl();
    }

    @Override
    public String accessToken() {
        return config.getAccessTokenUrl();
    }

    @Override
    public String userInfo() {
        return config.getUserInfoUrl();
    }

    @Override
    public String getName() {
        return name;
    }

    public OAuthConfig.CustomOAuthConfig getConfig() {
        return config;
    }
}
