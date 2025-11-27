package top.hcode.hoj.oauth;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthDefaultRequest;
import top.hcode.hoj.config.OAuthConfig;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: 自定义 OAuth 请求实现
 */
public class CustomAuthRequest extends AuthDefaultRequest {

    private final OAuthConfig.CustomOAuthConfig customConfig;

    public CustomAuthRequest(AuthConfig config, AuthStateCache authStateCache,
                             CustomAuthSource source) {
        super(config, source, authStateCache);
        this.customConfig = source.getConfig();
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        String response = doPostAuthorizationCode(authCallback.getCode());
        JSONObject accessTokenObject = JSONUtil.parseObj(response);

        this.checkResponse(accessTokenObject);

        return AuthToken.builder()
                .accessToken(accessTokenObject.getStr("access_token"))
                .refreshToken(accessTokenObject.getStr("refresh_token"))
                .expireIn(accessTokenObject.getInt("expires_in"))
                .tokenType(accessTokenObject.getStr("token_type"))
                .build();
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        String response = doGetUserInfo(authToken);
        JSONObject userInfo = JSONUtil.parseObj(response);

        this.checkResponse(userInfo);

        return AuthUser.builder()
                .uuid(userInfo.getStr(customConfig.getUserIdField()))
                .username(userInfo.getStr(customConfig.getUsernameField()))
                .nickname(userInfo.getStr(customConfig.getNicknameField()))
                .avatar(userInfo.getStr(customConfig.getAvatarField()))
                .email(userInfo.getStr(customConfig.getEmailField()))
                .token(authToken)
                .source(source.getName())
                .rawUserInfo(userInfo)
                .build();
    }

    private void checkResponse(JSONObject object) {
        if (object.containsKey("error")) {
            throw new RuntimeException(object.getStr("error_description", object.getStr("error")));
        }
    }

    @Override
    public String authorize(String state) {
        return String.format("%s?client_id=%s&redirect_uri=%s&response_type=code&state=%s&scope=%s",
                source.authorize(),
                config.getClientId(),
                config.getRedirectUri(),
                state,
                customConfig.getScope() != null ? customConfig.getScope() : "");
    }
}
