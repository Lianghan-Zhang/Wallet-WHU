package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Notebook;
import com.example.mapper.NotebookMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 记账日记业务处理
 **/
@Service
public class NotebookService {

    @Resource
    private NotebookMapper notebookMapper;

    /**
     * 新增
     */
    public void add(Notebook notebook) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            notebook.setUserId(currentUser.getId());
        }
        notebook.setDate(DateUtil.today());
        notebookMapper.insert(notebook);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        notebookMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            notebookMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Notebook notebook) {
        notebookMapper.updateById(notebook);
    }

    /**
     * 根据ID查询
     */
    public Notebook selectById(Integer id) {
        return notebookMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Notebook> selectAll(Notebook notebook) {
        return notebookMapper.selectAll(notebook);
    }

    /**
     * 分页查询
     */
    public PageInfo<Notebook> selectPage(Notebook notebook, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {  // 筛选用户自己的记账日记
            notebook.setUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Notebook> list = notebookMapper.selectAll(notebook);
        return PageInfo.of(list);
    }

}