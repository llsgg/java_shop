package com.gk.study.Vo;

import com.gk.study.entity.Good;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品返回对象
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/4/11 21:38
 */
@Data
public class GoodsVo extends Good {
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
