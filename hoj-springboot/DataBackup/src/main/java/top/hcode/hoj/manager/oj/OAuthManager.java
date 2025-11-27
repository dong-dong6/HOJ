package top.hcode.hoj.manager.oj;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;
import org.springframework.stereotype.Component;
import top.hcode.hoj.common.exception.StatusFailException;
import top.hcode.hoj.common.exception.StatusForbiddenException;
import top.hcode.hoj.config.OAuthConfig;
import top.hcode.hoj.dao.user.UserInfoEntityService;
import top.hcode.hoj.dao.user.UserRecordEntityService;
import top.hcode.hoj.dao.user.UserRoleEntityService;
import top.hcode.hoj.dao.user.UserThirdAuthEntityService;
import top.hcode.hoj.oauth.AuthStateRedisCache;
import top.hcode.hoj.oauth.CustomAuthRequest;
import top.hcode.hoj.oauth.CustomAuthSource;
import top.hcode.hoj.pojo.dto.OAuthBindDTO;
import top.hcode.hoj.pojo.dto.OAuthResultDTO;
import top.hcode.hoj.pojo.entity.user.*;
import top.hcode.hoj.pojo.vo.UserInfoVO;
import top.hcode.hoj.pojo.vo.UserRolesVO;
import top.hcode.hoj.shiro.AccountProfile;
import top.hcode.hoj.utils.AccountUtils;
import top.hcode.hoj.utils.RedisUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: OAuth2 第三方登录 Manager
 */
@Component
public class OAuthManager {

    private static final String BIND_TOKEN_PREFIX = "oauth:bind:";
    private static final long BIND_TOKEN_EXPIRE = 600; // 10分钟

    @Resource
    private OAuthConfig oAuthConfig;

    @Resource
    private AuthStateRedisCache authStateCache;

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UserThirdAuthEntityService userThirdAuthEntityService;

    @Resource
    private UserInfoEntityService userInfoEntityService;

    @Resource
    private UserRoleEntityService userRoleEntityService;

    @Resource
    private UserRecordEntityService userRecordEntityService;

    /**
     * 获取授权URL
     */
    public String getAuthorizeUrl(String platform) throws StatusFailException {
        if (!oAuthConfig.getEnabled()) {
            throw new StatusFailException("第三方登录功能未开启");
        }
        AuthRequest authRequest = getAuthRequest(platform);
        return authRequest.authorize(IdUtil.fastSimpleUUID());
    }

    /**
     * 获取已启用的第三方登录平台列表
     */
    public List<Map<String, String>> getEnabledPlatforms() {
        List<Map<String, String>> platforms = new java.util.ArrayList<>();
        
        if (!oAuthConfig.getEnabled()) {
            return platforms;
        }

        if (oAuthConfig.getGithub() != null && Boolean.TRUE.equals(oAuthConfig.getGithub().getEnabled())) {
            Map<String, String> github = new HashMap<>();
            github.put("platform", "github");
            github.put("name", "GitHub");
            platforms.add(github);
        }

        if (oAuthConfig.getGitee() != null && Boolean.TRUE.equals(oAuthConfig.getGitee().getEnabled())) {
            Map<String, String> gitee = new HashMap<>();
            gitee.put("platform", "gitee");
            gitee.put("name", "Gitee");
            platforms.add(gitee);
        }

        if (oAuthConfig.getGitlab() != null && Boolean.TRUE.equals(oAuthConfig.getGitlab().getEnabled())) {
            Map<String, String> gitlab = new HashMap<>();
            gitlab.put("platform", "gitlab");
            gitlab.put("name", "GitLab");
            platforms.add(gitlab);
        }

        if (oAuthConfig.getCustoms() != null) {
            oAuthConfig.getCustoms().forEach((key, config) -> {
                if (Boolean.TRUE.equals(config.getEnabled())) {
                    Map<String, String> custom = new HashMap<>();
                    custom.put("platform", key);
                    custom.put("name", config.getName() != null ? config.getName() : key);
                    platforms.add(custom);
                }
            });
        }

        return platforms;
    }

    /**
     * 处理OAuth回调
     */
    public OAuthResultDTO handleCallback(String platform, AuthCallback callback) throws StatusFailException {
        if (!oAuthConfig.getEnabled()) {
            throw new StatusFailException("第三方登录功能未开启");
        }

        AuthRequest authRequest = getAuthRequest(platform);
        AuthResponse<AuthUser> response = authRequest.login(callback);

        if (!response.ok()) {
            throw new StatusFailException("第三方登录失败：" + response.getMsg());
        }

        AuthUser authUser = response.getData();
        OAuthResultDTO result = new OAuthResultDTO();
        result.setPlatform(platform);
        result.setOpenId(authUser.getUuid());
        result.setUsername(authUser.getUsername());
        result.setNickname(authUser.getNickname());
        result.setAvatar(authUser.getAvatar());
        result.setEmail(authUser.getEmail());

        // 查询是否已绑定账号
        UserThirdAuth thirdAuth = userThirdAuthEntityService.getByPlatformAndOpenId(platform, authUser.getUuid());

        if (thirdAuth != null) {
            // 已绑定，直接登录
            return loginByThirdAuth(thirdAuth, authUser, result);
        } else {
            // 未绑定，返回绑定信息
            String bindToken = IdUtil.fastSimpleUUID();
            Map<String, Object> bindInfo = new HashMap<>();
            bindInfo.put("platform", platform);
            bindInfo.put("authUser", JSONUtil.toJsonStr(authUser));
            redisUtils.set(BIND_TOKEN_PREFIX + bindToken, JSONUtil.toJsonStr(bindInfo), BIND_TOKEN_EXPIRE);

            result.setNeedBind(true);
            result.setBindToken(bindToken);
            return result;
        }
    }

    /**
     * 绑定第三方账号
     */
    public OAuthResultDTO bindAccount(OAuthBindDTO bindDTO) throws StatusFailException, StatusForbiddenException {
        String bindInfoStr = (String) redisUtils.get(BIND_TOKEN_PREFIX + bindDTO.getBindToken());
        if (bindInfoStr == null) {
            throw new StatusFailException("绑定凭证已过期，请重新登录");
        }

        Map<String, Object> bindInfo = JSONUtil.toBean(bindInfoStr, Map.class);
        String platform = (String) bindInfo.get("platform");
        AuthUser authUser = JSONUtil.toBean((String) bindInfo.get("authUser"), AuthUser.class);

        String uid;
        if ("register".equals(bindDTO.getBindType())) {
            // 注册新账号
            uid = registerNewUser(bindDTO, authUser);
        } else if ("bindExist".equals(bindDTO.getBindType())) {
            // 绑定已有账号
            uid = bindExistingUser(bindDTO);
        } else {
            throw new StatusFailException("不支持的绑定方式");
        }

        // 创建绑定关系
        UserThirdAuth thirdAuth = new UserThirdAuth();
        thirdAuth.setUid(uid);
        thirdAuth.setPlatform(platform);
        thirdAuth.setOpenId(authUser.getUuid());
        thirdAuth.setUsername(authUser.getUsername());
        thirdAuth.setNickname(authUser.getNickname());
        thirdAuth.setAvatar(authUser.getAvatar());
        thirdAuth.setEmail(authUser.getEmail());
        thirdAuth.setRawUserInfo(JSONUtil.toJsonStr(authUser.getRawUserInfo()));
        if (authUser.getToken() != null) {
            thirdAuth.setAccessToken(authUser.getToken().getAccessToken());
            thirdAuth.setRefreshToken(authUser.getToken().getRefreshToken());
            thirdAuth.setExpireIn((long) authUser.getToken().getExpireIn());
        }
        userThirdAuthEntityService.save(thirdAuth);

        // 删除绑定凭证
        redisUtils.del(BIND_TOKEN_PREFIX + bindDTO.getBindToken());

        // 执行登录
        OAuthResultDTO result = new OAuthResultDTO();
        result.setPlatform(platform);
        result.setNeedBind(false);
        return loginByThirdAuth(thirdAuth, authUser, result);
    }

    /**
     * 获取当前用户的第三方账号绑定列表
     */
    public List<Map<String, Object>> getBindList() {
        AccountProfile profile = AccountUtils.getProfile();
        if (profile == null) {
            return new java.util.ArrayList<>();
        }
        
        List<UserThirdAuth> authList = userThirdAuthEntityService.getByUid(profile.getUid());
        return authList.stream().map(auth -> {
            Map<String, Object> map = new HashMap<>();
            map.put("platform", auth.getPlatform());
            map.put("username", auth.getUsername());
            map.put("nickname", auth.getNickname());
            map.put("avatar", auth.getAvatar());
            map.put("gmtCreate", auth.getGmtCreate());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 解绑第三方账号
     */
    public void unbind(String platform) throws StatusFailException {
        AccountProfile profile = AccountUtils.getProfile();
        if (profile == null) {
            throw new StatusFailException("请先登录");
        }

        UserThirdAuth thirdAuth = userThirdAuthEntityService.getByUidAndPlatform(profile.getUid(), platform);
        if (thirdAuth == null) {
            throw new StatusFailException("未绑定该平台账号");
        }

        userThirdAuthEntityService.unbind(profile.getUid(), platform);
    }

    /**
     * 通过第三方账号登录
     */
    private OAuthResultDTO loginByThirdAuth(UserThirdAuth thirdAuth, AuthUser authUser, OAuthResultDTO result) 
            throws StatusFailException {
        UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(thirdAuth.getUid(), null);
        if (userRolesVo == null) {
            throw new StatusFailException("账号不存在，请重新绑定");
        }

        if (userRolesVo.getStatus() != 0) {
            throw new StatusFailException("该账户已被封禁，请联系管理员处理");
        }

        // 更新第三方账号信息
        thirdAuth.setUsername(authUser.getUsername());
        thirdAuth.setNickname(authUser.getNickname());
        thirdAuth.setAvatar(authUser.getAvatar());
        thirdAuth.setEmail(authUser.getEmail());
        if (authUser.getToken() != null) {
            thirdAuth.setAccessToken(authUser.getToken().getAccessToken());
            thirdAuth.setRefreshToken(authUser.getToken().getRefreshToken());
            thirdAuth.setExpireIn((long) authUser.getToken().getExpireIn());
        }
        userThirdAuthEntityService.updateById(thirdAuth);

        // 执行登录
        StpUtil.login(userRolesVo.getUid());
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(userRolesVo, profile);
        profile.setUid(userRolesVo.getUid());
        AccountUtils.setProfile(profile);

        result.setNeedBind(false);
        result.setToken(StpUtil.getTokenValue());

        UserInfoVO userInfoVo = new UserInfoVO();
        BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
        userInfoVo.setRoleList(userRolesVo.getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toList()));
        result.setUserInfo(userInfoVo);

        return result;
    }

    /**
     * 注册新用户
     */
    private String registerNewUser(OAuthBindDTO bindDTO, AuthUser authUser) throws StatusFailException {
        if (bindDTO.getUsername() == null || bindDTO.getUsername().trim().isEmpty()) {
            throw new StatusFailException("用户名不能为空");
        }
        if (bindDTO.getPassword() == null || bindDTO.getPassword().length() < 6) {
            throw new StatusFailException("密码长度不能少于6位");
        }

        // 检查用户名是否已存在
        UserRolesVO existUser = userRoleEntityService.getUserRoles(null, bindDTO.getUsername().trim());
        if (existUser != null) {
            throw new StatusFailException("用户名已存在");
        }

        String uuid = IdUtil.simpleUUID();
        UserInfo userInfo = new UserInfo();
        userInfo.setUuid(uuid);
        userInfo.setUsername(bindDTO.getUsername().trim());
        userInfo.setPassword(SecureUtil.md5(bindDTO.getPassword().trim()));
        userInfo.setNickname(authUser.getNickname() != null ? authUser.getNickname() : bindDTO.getUsername().trim());
        userInfo.setEmail(bindDTO.getEmail());
        userInfo.setAvatar(authUser.getAvatar());
        userInfo.setStatus(0);
        userInfoEntityService.save(userInfo);

        // 添加用户角色
        userRoleEntityService.save(new UserRole().setRoleId(1002L).setUid(uuid));

        // 添加用户记录
        userRecordEntityService.save(new UserRecord().setUid(uuid));

        return uuid;
    }

    /**
     * 绑定已有用户
     */
    private String bindExistingUser(OAuthBindDTO bindDTO) throws StatusFailException, StatusForbiddenException {
        if (bindDTO.getUsername() == null || bindDTO.getUsername().trim().isEmpty()) {
            throw new StatusFailException("用户名不能为空");
        }
        if (bindDTO.getPassword() == null || bindDTO.getPassword().trim().isEmpty()) {
            throw new StatusFailException("密码不能为空");
        }

        UserRolesVO userRolesVo = userRoleEntityService.getUserRoles(null, bindDTO.getUsername().trim());
        if (userRolesVo == null) {
            throw new StatusFailException("用户名或密码错误");
        }

        if (!userRolesVo.getPassword().equals(SecureUtil.md5(bindDTO.getPassword().trim()))) {
            throw new StatusFailException("用户名或密码错误");
        }

        if (userRolesVo.getStatus() != 0) {
            throw new StatusForbiddenException("该账户已被封禁");
        }

        return userRolesVo.getUid();
    }

    /**
     * 获取 AuthRequest
     */
    private AuthRequest getAuthRequest(String platform) throws StatusFailException {
        switch (platform.toLowerCase()) {
            case "github":
                return getGithubAuthRequest();
            case "gitee":
                return getGiteeAuthRequest();
            case "gitlab":
                return getGitlabAuthRequest();
            default:
                return getCustomAuthRequest(platform);
        }
    }

    private AuthRequest getGithubAuthRequest() throws StatusFailException {
        OAuthConfig.OAuthClientConfig config = oAuthConfig.getGithub();
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            throw new StatusFailException("GitHub登录未启用");
        }
        return new AuthGithubRequest(AuthConfig.builder()
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .redirectUri(config.getRedirectUri())
                .build(), authStateCache);
    }

    private AuthRequest getGiteeAuthRequest() throws StatusFailException {
        OAuthConfig.OAuthClientConfig config = oAuthConfig.getGitee();
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            throw new StatusFailException("Gitee登录未启用");
        }
        return new AuthGiteeRequest(AuthConfig.builder()
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .redirectUri(config.getRedirectUri())
                .build(), authStateCache);
    }

    private AuthRequest getGitlabAuthRequest() throws StatusFailException {
        OAuthConfig.OAuthClientConfig config = oAuthConfig.getGitlab();
        if (config == null || !Boolean.TRUE.equals(config.getEnabled())) {
            throw new StatusFailException("GitLab登录未启用");
        }
        return new AuthGitlabRequest(AuthConfig.builder()
                .clientId(config.getClientId())
                .clientSecret(config.getClientSecret())
                .redirectUri(config.getRedirectUri())
                .build(), authStateCache);
    }

    private AuthRequest getCustomAuthRequest(String platform) throws StatusFailException {
        if (oAuthConfig.getCustoms() == null || !oAuthConfig.getCustoms().containsKey(platform)) {
            throw new StatusFailException("不支持的登录平台：" + platform);
        }

        OAuthConfig.CustomOAuthConfig customConfig = oAuthConfig.getCustoms().get(platform);
        if (!Boolean.TRUE.equals(customConfig.getEnabled())) {
            throw new StatusFailException(platform + "登录未启用");
        }

        CustomAuthSource source = new CustomAuthSource(platform, customConfig);
        return new CustomAuthRequest(AuthConfig.builder()
                .clientId(customConfig.getClientId())
                .clientSecret(customConfig.getClientSecret())
                .redirectUri(customConfig.getRedirectUri())
                .build(), authStateCache, source);
    }
}
