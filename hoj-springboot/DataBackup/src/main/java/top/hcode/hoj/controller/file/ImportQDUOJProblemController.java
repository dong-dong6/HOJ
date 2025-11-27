package top.hcode.hoj.controller.file;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.service.file.ImportQDUOJProblemService;

/**
 * @Author: Himit_ZH
 * @Date: 2021/10/5 19:44
 * @Description:
 */

@Controller
@RequestMapping("/api/file")
public class ImportQDUOJProblemController {

    @Autowired
    private ImportQDUOJProblemService importQDUOJProblemService;

    /**
     * @param file
     * @MethodName importQDOJProblem
     * @Description zip文件导入题目 仅超级管理员可操作
     * @Return
     * @Since 2021/5/27
     */
    @SaCheckRole("root")
    @SaCheckLogin
    @ResponseBody
    @PostMapping("/import-qdoj-problem")
    public CommonResult<Void> importQDOJProblem(@RequestParam("file") MultipartFile file) {
        return importQDUOJProblemService.importQDOJProblem(file);
    }


}