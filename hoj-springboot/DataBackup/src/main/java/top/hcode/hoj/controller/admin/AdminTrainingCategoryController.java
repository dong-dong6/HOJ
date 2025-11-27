package top.hcode.hoj.controller.admin;


import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import org.springframework.web.bind.annotation.*;
import top.hcode.hoj.common.result.CommonResult;

import top.hcode.hoj.pojo.entity.training.TrainingCategory;
import top.hcode.hoj.service.admin.training.AdminTrainingCategoryService;

import javax.annotation.Resource;

/**
 * @Author: Himit_ZH
 * @Date: 2021/11/27 15:11
 * @Description:
 */

@RestController
@RequestMapping("/api/admin/training/category")
public class AdminTrainingCategoryController {

    @Resource
    private AdminTrainingCategoryService adminTrainingCategoryService;

    @PostMapping("")
    @SaCheckLogin
    @SaCheckRole(value = {"root", "problem_admin"}, mode = SaMode.OR)
    public CommonResult<TrainingCategory> addTrainingCategory(@RequestBody TrainingCategory trainingCategory) {
        return adminTrainingCategoryService.addTrainingCategory(trainingCategory);
    }

    @PutMapping("")
    @SaCheckLogin
    @SaCheckRole(value = {"root", "problem_admin"}, mode = SaMode.OR)
    public CommonResult<Void> updateTrainingCategory(@RequestBody TrainingCategory trainingCategory) {
        return adminTrainingCategoryService.updateTrainingCategory(trainingCategory);
    }

    @DeleteMapping("")
    @SaCheckLogin
    @SaCheckRole(value = {"root", "problem_admin"}, mode = SaMode.OR)
    public CommonResult<Void> deleteTrainingCategory(@RequestParam("cid") Long cid) {
        return adminTrainingCategoryService.deleteTrainingCategory(cid);
    }
}