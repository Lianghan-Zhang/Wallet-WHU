package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.AcTypeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Ac;
import com.example.entity.Account;
import com.example.entity.Bill;
import com.example.mapper.BillMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 账单表业务处理
 **/
@Service
public class BillService {

    @Resource
    private BillMapper billMapper;
    @Resource
    AcService acService;

    /**
     * 新增
     */
    @Transactional
    public void add(Bill bill) {
        bill.setTime(DateUtil.now());
        if (ObjectUtil.isEmpty(bill.getUserId())) {
            Account currentUser = TokenUtils.getCurrentUser();
            if (RoleEnum.USER.name().equals(currentUser.getRole())) {  // 只有用户才记录用户ID
                bill.setUserId(currentUser.getId());
            }
            billMapper.insert(bill);
            // 开户
            Ac ac = acService.selectByUserId(currentUser.getId());
            if (ac == null) {
                ac = new Ac();  // 创建账号
                ac.setUserId(currentUser.getId());
                ac.setBalance(BigDecimal.ZERO);
                ac.setPay(BigDecimal.ZERO);
                ac.setIncome(BigDecimal.ZERO);
                acService.add(ac);
            }

            // 更新账户信息
            if (AcTypeEnum.PAY.getValue().equals(bill.getType())) {  // 支出
                ac.setPay(ac.getPay().add(bill.getMoney()));
                ac.setBalance(ac.getBalance().subtract(bill.getMoney()));  // 余额
            } else {
                ac.setIncome(ac.getIncome().add(bill.getMoney()));  // 收入
                ac.setBalance(ac.getBalance().add(bill.getMoney()));  // 余额
            }
            acService.updateById(ac);
        } else {
            billMapper.insert(bill);
            // 开户
            Ac ac = acService.selectByUserId(bill.getUserId());
            if (ac == null) {
                ac = new Ac();  // 创建账号
                ac.setUserId(bill.getUserId());
                ac.setBalance(BigDecimal.ZERO);
                ac.setPay(BigDecimal.ZERO);
                ac.setIncome(BigDecimal.ZERO);
                acService.add(ac);
            }

            // 更新账户信息
            if (AcTypeEnum.PAY.getValue().equals(bill.getType())) {  // 支出
                ac.setPay(ac.getPay().add(bill.getMoney()));
                ac.setBalance(ac.getBalance().subtract(bill.getMoney()));  // 余额
            } else {
                ac.setIncome(ac.getIncome().add(bill.getMoney()));  // 收入
                ac.setBalance(ac.getBalance().add(bill.getMoney()));  // 余额
            }
            acService.updateById(ac);
        }




    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        billMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            billMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Bill bill) {
        billMapper.updateById(bill);
    }

    /**
     * 根据ID查询
     */
    public Bill selectById(Integer id) {
        return billMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Bill> selectAll(Bill bill) {
        return billMapper.selectAll(bill);
    }

    /**
     * 分页查询
     */
    public PageInfo<Bill> selectPage(Bill bill, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {  // 筛选用户自己的账单信息
            bill.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Bill> list = billMapper.selectAll(bill);
        return PageInfo.of(list);
    }

    public List<Bill> count(String type) {
        Bill bill = new Bill();
        Account currentUser = TokenUtils.getCurrentUser();
        bill.setUserId(currentUser.getId());
        bill.setType(type);
        List<Bill> billList = billMapper.selectAll(bill);  // 根据当前登录的用户ID查询到所有的账单信息
        BigDecimal sum = billList.stream().map(Bill::getMoney).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);// 获取当前类型的所有的账单金额
        List<String> categoryList = billMapper.selectCategoryByType(type);
        List<Bill> list = new ArrayList<>();
        for (String category : categoryList) {
            Bill b = new Bill();
            b.setCategory(category);
            // 统计出当前这个分类的所有的金额汇总
            BigDecimal categorySum = billList.stream().filter(bi -> bi.getCategory().equals(category)).map(Bill::getMoney).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            // 返回当前分类的总金额
            b.setMoney(categorySum);
            // 得到账单的百分比
            b.setPercent(categorySum.divide(sum, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).intValue());
            list.add(b);
        }
        return list;
    }

}