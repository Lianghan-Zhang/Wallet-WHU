package com.example.mapper;

import com.example.entity.Notebook;
import java.util.List;

/**
 * 操作notebook相关数据接口
 */
public interface NotebookMapper {

    /**
     * 新增
     */
    int insert(Notebook notebook);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Notebook notebook);

    /**
     * 根据ID查询
     */
    Notebook selectById(Integer id);

    /**
     * 查询所有
     */
    List<Notebook> selectAll(Notebook notebook);

}