package top.hcode.hoj.controller.oj;

import cn.dev33.satoken.annotation.SaCheckLogin;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.zhyd.oauth.model.AuthCallback;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.annotation.AnonApi;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.manager.oj.OAuthManager;
import top.hcode.hoj.pojo.dto.OAuthBindDTO;
import top.hcode.hoj.pojo.dto.OAuthResultDTO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: Himit_ZH
 * @Date: 2024/01/01
 * @Description: OAuth2 第三方登录 Controller
 */
@RestController
@RequestMapping("/api/oauth")
@Api(tags = "OAuth2第三方登录")
public class OAuthController {

    @Resource
    private OAuthManager oAuthManager;

    @GetMapping("/platforms")
    @ApiOperation("获取已启用的第三方登录平台列表")
    @AnonApi
    public CommonResult<List<Map<String, String>>> getPlatforms() {
        return CommonResult.successResponse(oAuthManager.getEnabledPlatforms());
    }

    @GetMapping("/authorize/{platform}")
    @ApiOperation("获取授权URL")
    @AnonApi
    public CommonResult<String> getAuthorizeUrl(@PathVariable("platform") String platform) {
        try {
            String url = oAuthManager.getAuthorizeUrl(platform);
            return CommonResult.successResponse(url);
        } catch (Exception e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @GetMapping("/redirect/{platform}")
    @ApiOperation("重定向到授权页面")
    @AnonApi
    public void redirect(@PathVariable("platform") String platform, HttpServletResponse response) throws IOException {
        try {
            String url = oAuthManager.getAuthorizeUrl(platform);
            response.sendRedirect(url);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/callback/{platform}")
    @ApiOperation("OAuth回调处理")
    @AnonApi
    public CommonResult<OAuthResultDTO> callback(@PathVariable("platform") String platform, AuthCallback callback) {
        try {
            OAuthResultDTO result = oAuthManager.handleCallback(platform, callback);
            return CommonResult.successResponse(result);
        } catch (Exception e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @PostMapping("/bind")
    @ApiOperation("绑定第三方账号到本地账号")
    @AnonApi
    public CommonResult<OAuthResultDTO> bind(@RequestBody OAuthBindDTO bindDTO) {
        try {
            OAuthResultDTO result = oAuthManager.bindAccount(bindDTO);
            return CommonResult.successResponse(result);
        } catch (Exception e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }

    @GetMapping("/bindList")
    @ApiOperation("获取当前用户已绑定的第三方账号列表")
    @SaCheckLogin
    public CommonResult<List<Map<String, Object>>> getBindList() {
        return CommonResult.successResponse(oAuthManager.getBindList());
    }

    @DeleteMapping("/unbind/{platform}")
    @ApiOperation("解绑第三方账号")
    @SaCheckLogin
    public CommonResult<Void> unbind(@PathVariable("platform") String platform) {
        try {
            oAuthManager.unbind(platform);
            return CommonResult.successResponse();
        } catch (Exception e) {
            return CommonResult.errorResponse(e.getMessage());
        }
    }
}
