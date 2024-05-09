package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
    @TableName("b_seckill_goods")
    public class SeckillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

            /**
            * 秒杀商品ID
            */
            @TableId(value = "id", type = IdType.AUTO)
    private Long id;


    private Long goodsId;
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;


}
