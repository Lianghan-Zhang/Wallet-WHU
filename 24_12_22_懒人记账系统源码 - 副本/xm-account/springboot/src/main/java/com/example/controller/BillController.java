package com.example.controller;

import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.example.common.Result;
import com.example.entity.Bill;
import com.example.service.BillService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 账单表前端操作接口
 **/
@RestController
@RequestMapping("/bill")
public class BillController {

    @Resource
    private BillService billService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Bill bill) {
        billService.add(bill);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        billService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        billService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Bill bill) {
        billService.updateById(bill);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Bill bill = billService.selectById(id);
        return Result.success(bill);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Bill bill) {
        List<Bill> list = billService.selectAll(bill);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Bill bill,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Bill> page = billService.selectPage(bill, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 批量导出
     */
    @GetMapping("export")
    public void export(@RequestParam(required = false) Integer userId,  HttpServletResponse response) throws IOException {
        List<Bill> billList;
        if (userId != null) {
            Bill bill = new Bill();
            bill.setUserId(userId);
            billList = billService.selectAll(bill);
        } else {
            billList = billService.selectAll(null);
        }
        ExcelWriter excelWriter = ExcelUtil.getWriter();
        excelWriter.addHeaderAlias("category", "账单分类");
        excelWriter.addHeaderAlias("payType", "账户类型");
        excelWriter.addHeaderAlias("money", "金额");
        excelWriter.addHeaderAlias("type", "账单类型");
        excelWriter.addHeaderAlias("comment", "备注");
        excelWriter.addHeaderAlias("time", "时间");
        excelWriter.addHeaderAlias("userName", "用户名称");
        excelWriter.setOnlyAlias(true);  // 默认的，未添加alias的属性也会写出，如果想只写出加了别名的字段，可以调用此方法排除之

        excelWriter.write(billList);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode("账单数据", "UTF-8") + ".xlsx");
        ServletOutputStream os = response.getOutputStream();
        excelWriter.flush(os, true);
        excelWriter.close();
        os.close();
    }

    // 统计账单的比例情况
    @GetMapping("/count")
    public Result count(String type) {
        List<Bill> list = billService.count(type);
        return Result.success(list);
    }

}