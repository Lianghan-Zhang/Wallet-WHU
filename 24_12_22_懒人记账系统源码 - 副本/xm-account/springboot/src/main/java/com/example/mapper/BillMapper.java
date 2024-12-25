package com.example.mapper;

import com.example.entity.Bill;
import java.util.List;

/**
 * 操作bill相关数据接口
 */
public interface BillMapper {

    /**
     * 新增
     */
    int insert(Bill bill);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Bill bill);

    /**
     * 根据ID查询
     */
    Bill selectById(Integer id);

    /**
     * 查询所有
     */
    List<Bill> selectAll(Bill bill);

    List<String> selectCategoryByType(String type);

}