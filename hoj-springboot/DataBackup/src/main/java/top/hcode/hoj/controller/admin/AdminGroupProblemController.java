package top.hcode.hoj.controller.admin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;
import top.hcode.hoj.pojo.dto.ChangeGroupProblemProgressDTO;
import top.hcode.hoj.pojo.entity.problem.Problem;
import top.hcode.hoj.service.admin.problem.AdminGroupProblemService;

import javax.annotation.Resource;

/**
 * @Author Himit_ZH
 * @Date 2022/4/13
 */
@RestController
@RequestMapping("/api/admin/group-problem")
@SaCheckLogin
@SaCheckRole(value = {"root", "problem_admin"}, mode = SaMode.OR)
public class AdminGroupProblemController {

    @Resource
    private AdminGroupProblemService adminGroupProblemService;

    @GetMapping("/list")
    public CommonResult<IPage<Problem>> getProblemList(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                                                       @RequestParam(value = "limit", defaultValue = "10") Integer limit,
                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                       @RequestParam(value = "gid", required = false) Long gid) {
        return adminGroupProblemService.getProblemList(currentPage, limit, keyword, gid);
    }

    @PutMapping("/change-progress")
    public CommonResult<Void> changeProgress(@RequestBody ChangeGroupProblemProgressDTO changeGroupProblemProgressDto) {
        return adminGroupProblemService.changeProgress(changeGroupProblemProgressDto);
    }
}
