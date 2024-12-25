package com.example.entity;

import java.io.Serializable;

/**
 * 记账日记
 */
public class Notebook implements Serializable {
    private static final long serialVersionUID = 1L;

    /**  */
    private Integer id;
    /** 标题 */
    private String title;
    /** 封面 */
    private String cover;
    /** 内容 */
    private String content;
    /** 用户ID */
    private Integer userId;
    /** 发布日期 */
    private String date;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}