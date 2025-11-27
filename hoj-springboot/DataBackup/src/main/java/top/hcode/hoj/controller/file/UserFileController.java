package top.hcode.hoj.controller.file;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.hcode.hoj.service.file.UserFileService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: Himit_ZH
 * @Date: 2021/10/5 19:48
 * @Description:
 */
@Controller
@RequestMapping("/api/file")
public class UserFileController {

    @Autowired
    private UserFileService userFileService;

    @RequestMapping("/generate-user-excel")
    @SaCheckLogin
    @SaCheckRole("root")
    public void generateUserExcel(@RequestParam("key") String key, HttpServletResponse response) throws IOException {
        userFileService.generateUserExcel(key, response);
    }

}