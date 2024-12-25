package com.example.controller;

import com.example.common.Result;
import com.example.entity.PlanDetail;
import com.example.service.PlanDetailService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 计划明细前端操作接口
 **/
@RestController
@RequestMapping("/planDetail")
public class PlanDetailController {

    @Resource
    private PlanDetailService planDetailService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody PlanDetail planDetail) {
        planDetailService.add(planDetail);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        planDetailService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        planDetailService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody PlanDetail planDetail) {
        planDetailService.updateById(planDetail);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        PlanDetail planDetail = planDetailService.selectById(id);
        return Result.success(planDetail);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(PlanDetail planDetail) {
        List<PlanDetail> list = planDetailService.selectAll(planDetail);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(PlanDetail planDetail,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<PlanDetail> page = planDetailService.selectPage(planDetail, pageNum, pageSize);
        return Result.success(page);
    }

}