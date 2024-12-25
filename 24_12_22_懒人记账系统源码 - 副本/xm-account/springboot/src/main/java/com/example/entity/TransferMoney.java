package com.example.entity;

import java.math.BigDecimal;

/**
 * 转账记录
*/
public class TransferMoney {
    /** 主键ID */
    private Integer id;
    /** 打款方id */
    private Integer draweeId;
    /** 打款方名称 */
    private String draweeName;
    /** 收款方id */
    private Integer payeeId;
    /** 收款方名称 */
    private String payeeName;
    /** 金额 */
    private BigDecimal price;
    /** 备注 */
    private String remark;
    /** 时间 */
    private String time;
    /** 时间 */
    private String startTime;
    /** 时间 */
    private String endTime;
    /** 转账状态 */
    private String status;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDraweeName() {
        return draweeName;
    }

    public void setDraweeName(String draweeName) {
        this.draweeName = draweeName;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDraweeId() {
        return draweeId;
    }

    public void setDraweeId(Integer draweeId) {
        this.draweeId = draweeId;
    }

    public Integer getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Integer payeeId) {
        this.payeeId = payeeId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}