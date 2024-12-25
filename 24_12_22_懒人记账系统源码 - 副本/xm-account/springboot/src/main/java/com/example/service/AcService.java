package com.example.service;

import com.example.entity.Ac;
import com.example.mapper.AcMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 账户业务处理
 **/
@Service
public class AcService {

    @Resource
    private AcMapper acMapper;

    /**
     * 新增
     */
    public void add(Ac ac) {
        acMapper.insert(ac);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        acMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            acMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Ac ac) {
        acMapper.updateById(ac);
    }

    /**
     * 根据ID查询
     */
    public Ac selectById(Integer id) {
        return acMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Ac> selectAll(Ac ac) {
        return acMapper.selectAll(ac);
    }

    /**
     * 分页查询
     */
    public PageInfo<Ac> selectPage(Ac ac, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Ac> list = acMapper.selectAll(ac);
        return PageInfo.of(list);
    }

    public Ac selectByUserId(Integer userId) {
       return acMapper.selectByUserId(userId);
    }
}