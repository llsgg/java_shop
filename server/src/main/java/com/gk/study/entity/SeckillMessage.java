package com.gk.study.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 秒杀信息类
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/4/22 23:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMessage {
    private String orderNumber;
    private Long userId;
    private Long goodsId;
}
