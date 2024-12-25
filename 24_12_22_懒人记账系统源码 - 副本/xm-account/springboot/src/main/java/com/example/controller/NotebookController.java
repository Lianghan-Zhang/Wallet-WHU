package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Notebook;
import com.example.service.NotebookService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;

/**
 * 记账日记前端操作接口
 **/
@RestController
@RequestMapping("/notebook")
public class NotebookController {

    @Resource
    private NotebookService notebookService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Notebook notebook) {
        notebookService.add(notebook);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        notebookService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        notebookService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Notebook notebook) {
        notebookService.updateById(notebook);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Notebook notebook = notebookService.selectById(id);
        return Result.success(notebook);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Notebook notebook) {
        List<Notebook> list = notebookService.selectAll(notebook);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Notebook notebook,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Notebook> page = notebookService.selectPage(notebook, pageNum, pageSize);
        return Result.success(page);
    }

}