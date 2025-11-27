package top.hcode.hoj.controller.admin;

import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;

import top.hcode.hoj.pojo.dto.LoginDTO;

import top.hcode.hoj.pojo.vo.UserInfoVO;
import top.hcode.hoj.service.admin.account.AdminAccountService;



/**
 * @Author: Himit_ZH
 * @Date: 2020/12/2 21:23
 * @Description:
 */
@RestController
@RequestMapping("/api/admin")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminAccountService;

    @PostMapping("/login")
    public CommonResult<UserInfoVO> login(@Validated @RequestBody LoginDTO loginDto){
       return adminAccountService.login(loginDto);
    }

    @GetMapping("/logout")
    @SaCheckLogin
    @SaCheckRole(value = {"root","admin","problem_admin"},mode = SaMode.OR)
    public CommonResult<Void> logout() {
        return adminAccountService.logout();
    }

}