package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.*;
import com.example.exception.CustomException;
import com.example.mapper.AcMapper;
import com.example.mapper.BillMapper;
import com.example.mapper.TransferMoneyMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 转账记录业务处理
 **/
@Service
public class TransferMoneyService {

    @Resource
    private TransferMoneyMapper transferMoneyMapper;

    @Resource
    private AcMapper acMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private BillService billService;

    /**
     * 新增
     */
    public void add(TransferMoney transferMoney) {
        Account currentUser = TokenUtils.getCurrentUser();
        transferMoney.setDraweeId(currentUser.getId());
        transferMoney.setTime(DateUtil.now());
        transferMoney.setStatus("待处理");
//        判断用户余额是否充足
        Ac ac = acMapper.selectByUserId(currentUser.getId());
        if (ac.getBalance().compareTo(transferMoney.getPrice()) < 0) {
            throw new CustomException("500", "转款金额已超过用户现在余额");
        }
        transferMoneyMapper.insert(transferMoney);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        transferMoneyMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            transferMoneyMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    @Transactional
    public void updateById(TransferMoney transferMoney) {
        if (transferMoney.getStatus().equals("汇款成功")) {
            TransferMoney transferMoney1 = transferMoneyMapper.selectById(transferMoney.getId());
            if (ObjectUtil.isNotEmpty(transferMoney1)) {
                //            对方接收,生成支出账单
                Bill bill = new Bill();
                bill.setUserId(transferMoney1.getDraweeId());
                bill.setTime(DateUtil.now());
                bill.setComment(transferMoney1.getRemark());
                bill.setCategory("转账");
                bill.setType("支出");
                bill.setPayType("支付宝");
                bill.setMoney(transferMoney1.getPrice());
                billService.add(bill);

//            生成收入账单
                Bill bill1 = new Bill();
                bill1.setUserId(transferMoney1.getPayeeId());
                bill1.setTime(DateUtil.now());
                bill1.setComment(transferMoney1.getRemark());
                bill1.setType("收入");
                bill1.setCategory("转账");
                bill1.setPayType("支付宝");
                bill1.setMoney(transferMoney1.getPrice());
                billService.add(bill1);
            }

        }
        transferMoneyMapper.updateById(transferMoney);
    }

    /**
     * 根据ID查询
     */
    public TransferMoney selectById(Integer id) {
        return transferMoneyMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<TransferMoney> selectAll(TransferMoney transferMoney) {
        List<TransferMoney> list = transferMoneyMapper.selectAll(transferMoney);
        for (TransferMoney transferMoney1 : list) {
            User user = userMapper.selectById(transferMoney1.getDraweeId());
            if (ObjectUtil.isNotEmpty(user)) {
                transferMoney1.setDraweeName(user.getName());
            }
            User user1 = userMapper.selectById(transferMoney1.getPayeeId());
            if (ObjectUtil.isNotEmpty(user1)) {
                transferMoney1.setPayeeName(user1.getName());
            }
        }
        return list;
    }

    /**
     * 分页查询
     */
    public PageInfo<TransferMoney> selectPage(TransferMoney transferMoney, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<TransferMoney> list = this.selectAll(transferMoney);
        return PageInfo.of(list);
    }

}