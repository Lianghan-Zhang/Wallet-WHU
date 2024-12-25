package com.example.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.PlanDetail;
import com.example.mapper.PlanDetailMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 计划明细业务处理
 **/
@Service
public class PlanDetailService {

    @Resource
    private PlanDetailMapper planDetailMapper;

    /**
     * 新增
     */
    public void add(PlanDetail planDetail) {
        BigDecimal sum = this.getFirstSum(planDetail.getPlanId());
        // 再插入新的总额数据
        planDetail.setSum(sum.add(planDetail.getMoney()));
        planDetail.setDate(DateUtil.today());
        planDetailMapper.insert(planDetail);
    }

    /**
     * 获取最新的存储的总金额
     */
    public BigDecimal getFirstSum(Integer planId) {
        // 先查询已存入的总额
        PlanDetail param = new PlanDetail();
        param.setPlanId(planId);
        List<PlanDetail> planDetailList = planDetailMapper.selectAll(param);// 查询当前计划下所有的计划明细列表
        BigDecimal sum = BigDecimal.ZERO;
        if (CollUtil.isNotEmpty(planDetailList)) {
            PlanDetail detail = planDetailList.get(0);// 最新的一个存钱计划
            sum = detail.getSum();  // 获取最新的存储总额
        }
        return sum;
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        planDetailMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            planDetailMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(PlanDetail planDetail) {
        planDetailMapper.updateById(planDetail);
    }

    /**
     * 根据ID查询
     */
    public PlanDetail selectById(Integer id) {
        return planDetailMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<PlanDetail> selectAll(PlanDetail planDetail) {
        return planDetailMapper.selectAll(planDetail);
    }

    /**
     * 分页查询
     */
    public PageInfo<PlanDetail> selectPage(PlanDetail planDetail, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PlanDetail> list = planDetailMapper.selectAll(planDetail);
        return  PageInfo.of(list);
    }

}