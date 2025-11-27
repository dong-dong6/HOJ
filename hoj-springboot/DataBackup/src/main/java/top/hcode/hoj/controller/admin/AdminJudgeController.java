package top.hcode.hoj.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.entity.judge.Judge;
import top.hcode.hoj.service.admin.rejudge.RejudgeService;


import javax.annotation.Resource;

/**
 * @Author: Himit_ZH
 * @Date: 2021/1/3 14:09
 * @Description: 超管重判提交
 */

@RestController
@RequestMapping("/api/admin/judge")
public class AdminJudgeController {

    @Resource
    private RejudgeService rejudgeService;

    @GetMapping("/rejudge")
    @SaCheckLogin
    @SaCheckRole("root")  // 只有超级管理员能操作
    @SaCheckPermission("rejudge")
    public CommonResult<Judge> rejudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.rejudge(submitId);
    }

    @GetMapping("/rejudge-contest-problem")
    @SaCheckLogin
    @SaCheckRole("root")  // 只有超级管理员能操作
    @SaCheckPermission("rejudge")
    public CommonResult<Void> rejudgeContestProblem(@RequestParam("cid") Long cid, @RequestParam("pid") Long pid) {
        return rejudgeService.rejudgeContestProblem(cid, pid);
    }


    @GetMapping("/manual-judge")
    @SaCheckLogin
    @SaCheckRole("root")  // 只有超级管理员能操作
    @SaCheckPermission("rejudge")
    public CommonResult<Judge> manualJudge(@RequestParam("submitId") Long submitId,
                                           @RequestParam("status") Integer status,
                                           @RequestParam(value = "score", required = false) Integer score) {
        return rejudgeService.manualJudge(submitId, status, score);
    }

    @GetMapping("/cancel-judge")
    @SaCheckLogin
    @SaCheckRole("root")  // 只有超级管理员能操作
    @SaCheckPermission("rejudge")
    public CommonResult<Judge> cancelJudge(@RequestParam("submitId") Long submitId) {
        return rejudgeService.cancelJudge(submitId);
    }
}
