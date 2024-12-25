package com.example.controller;

import com.example.common.Result;
import com.example.entity.TransferMoney;
import com.example.service.TransferMoneyService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 转账记录前端操作接口
 **/
@RestController
@RequestMapping("/transferMoney")
public class TransferMoneyController {

    @Resource
    private TransferMoneyService transferMoneyService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody TransferMoney transferMoney) {
        transferMoneyService.add(transferMoney);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        transferMoneyService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        transferMoneyService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody TransferMoney transferMoney) {
        transferMoneyService.updateById(transferMoney);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        TransferMoney transferMoney = transferMoneyService.selectById(id);
        return Result.success(transferMoney);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(TransferMoney transferMoney) {
        List<TransferMoney> list = transferMoneyService.selectAll(transferMoney);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(TransferMoney transferMoney,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TransferMoney> page = transferMoneyService.selectPage(transferMoney, pageNum, pageSize);
        return Result.success(page);
    }

}