package com.example.mapper;

import com.example.entity.TransferMoney;

import java.util.List;

/**
 * 操作transfer_money相关数据接口
*/
public interface TransferMoneyMapper {

    /**
      * 新增
    */
    int insert(TransferMoney transferMoney);

    /**
      * 删除
    */
    int deleteById(Integer id);

    /**
      * 修改
    */
    int updateById(TransferMoney transferMoney);

    /**
      * 根据ID查询
    */
    TransferMoney selectById(Integer id);

    /**
      * 查询所有
    */
    List<TransferMoney> selectAll(TransferMoney transferMoney);

}