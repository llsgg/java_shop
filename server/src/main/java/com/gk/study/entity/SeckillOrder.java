package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
* <p>
    * 
    * </p>
*
* @author longlin
* @since 2024-04-11
*/
    @Data
        @EqualsAndHashCode(callSuper = false)
    @Accessors(chain = true)
    @TableName("b_seckill_order")
    public class SeckillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 秒杀订单ID
            */
            @TableId(value = "id", type = IdType.AUTO)
    private Long id;

            /**
            * 用户ID
            */
    private Long userId;

            /**
            * 订单ID
            */
    private Long orderId;

            /**
            * 商品ID
            */
    private Long goodsId;


}
