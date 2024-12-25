package com.example.mapper;

import com.example.entity.PlanDetail;

import java.util.List;

/**
 * 操作plan_detail相关数据接口
 */
public interface PlanDetailMapper {

    /**
     * 新增
     */
    int insert(PlanDetail planDetail);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(PlanDetail planDetail);

    /**
     * 根据ID查询
     */
    PlanDetail selectById(Integer id);

    /**
     * 查询所有
     */
    List<PlanDetail> selectAll(PlanDetail planDetail);

}