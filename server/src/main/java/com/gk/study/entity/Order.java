package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("b_order")
public class Order implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    @TableField
    public String status;
    @TableField
    public String orderTime;
    @TableField
    public String payTime;
    @TableField
    public String goodId;
    @TableField
    public String userId;
    @TableField
    public String count;
    @TableField
    public String orderNumber; // 订单编号

    @TableField(exist = false)
    public String username; // 用户名
    @TableField(exist = false)
    public String title; // 商品名称
    @TableField(exist = false)
    public String cover; // 商品封面
    @TableField(exist = false)
    public String price; // 商品价格

}
