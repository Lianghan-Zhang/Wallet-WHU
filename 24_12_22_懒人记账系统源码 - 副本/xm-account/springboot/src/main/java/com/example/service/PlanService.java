package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.PlanStatusEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Plan;
import com.example.exception.CustomException;
import com.example.mapper.PlanMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.math3.dfp.DfpField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

/**
 * 存钱计划业务处理
 **/
@Service
public class PlanService {

    @Resource
    private PlanMapper planMapper;

    @Resource
    PlanDetailService planDetailService;

    /**
     * 新增
     */
    public void add(Plan plan) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            plan.setUserId(currentUser.getId());
        }
        if (DateUtil.parseDate(plan.getStart()).isBefore(new Date())) {
            throw new CustomException(ResultCodeEnum.DATE_START_ERROR);
        }
        if (DateUtil.parseDate(plan.getStart()).isAfterOrEquals(DateUtil.parseDate(plan.getEnd()))) {
            throw new CustomException(ResultCodeEnum.DATE_ERROR);
        }
        plan.setDate(DateUtil.today());
        planMapper.insert(plan);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        planMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            planMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Plan plan) {
        planMapper.updateById(plan);
    }

    /**
     * 根据ID查询
     */
    public Plan selectById(Integer id) {
        Plan plan = planMapper.selectById(id);;
        this.setPlan(plan);
        return plan;
    }

    /**
     * 查询所有
     */
    public List<Plan> selectAll(Plan plan) {
        return planMapper.selectAll(plan);
    }

    /**
     * 分页查询
     */
    public PageInfo<Plan> selectPage(Plan plan, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Plan> list = planMapper.selectAll(plan);
        PageInfo<Plan> pageInfo = PageInfo.of(list);
        for (Plan p : pageInfo.getList()) {
            this.setPlan(p);
        }
        return pageInfo;
    }

    public void setPlan(Plan p) {
        BigDecimal sum = planDetailService.getFirstSum(p.getId());
        if (sum.compareTo(p.getMoney()) >= 0) {  // 存储的总额超过了计划金额
            p.setPercent(100);
        } else {
            int percent = sum.divide(p.getMoney(), 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).intValue();
            p.setPercent(percent);
        }
        Date today = new Date();
        if (DateUtil.parseDate(p.getStart()).isAfter(today)) {
            p.setStatus(PlanStatusEnum.NO_READY.getValue());  // 设置存钱的状态
        } else if (DateUtil.parseDate(p.getStart()).isBeforeOrEquals(today) && DateUtil.parseDate(p.getEnd()).isAfterOrEquals(today)) {
            p.setStatus(PlanStatusEnum.IN_PROGRESS.getValue());  // 设置存钱的状态
        } else if (DateUtil.parseDate(p.getEnd()).isBefore(today)) {
            p.setStatus(PlanStatusEnum.END.getValue());  // 设置存钱的状态
        }

        if (sum.compareTo(p.getMoney()) >= 0) {
            p.setStatus(PlanStatusEnum.DONE.getValue());  // 钱存够了 那就完成了
        }
    }

}