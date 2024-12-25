package com.example.mapper;

import com.example.entity.Ac;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 操作ac相关数据接口
 */
public interface AcMapper {

    /**
     * 新增
     */
    int insert(Ac ac);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Ac ac);

    /**
     * 根据ID查询
     */
    Ac selectById(Integer id);

    /**
     * 查询所有
     */
    List<Ac> selectAll(Ac ac);

    @Select("select * from ac where user_id = #{userId}")
    Ac selectByUserId(Integer userId);
}