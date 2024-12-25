package com.example.controller;

import com.example.common.Result;
import com.example.entity.Ac;
import com.example.service.AcService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 账户前端操作接口
 **/
@RestController
@RequestMapping("/ac")
public class AcController {

    @Resource
    private AcService acService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Ac ac) {
        acService.add(ac);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        acService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        acService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Ac ac) {
        acService.updateById(ac);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Ac ac = acService.selectById(id);
        return Result.success(ac);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Ac ac) {
        List<Ac> list = acService.selectAll(ac);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Ac ac,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Ac> page = acService.selectPage(ac, pageNum, pageSize);
        return Result.success(page);
    }

}