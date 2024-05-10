package com.gk.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gk.study.entity.SeckillOrder;
import com.gk.study.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author longlin
 * @since 2024-04-11
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    /**
     * @description:获取秒杀结果
     * @author: longlin
     * @date: 2024/4/23 11:42
     * @param: [user, goodsId]
     * @return: [user, goodsId] orderId:成功、-1：失败、0：派对中
     **/
    Long getResult(Long userId, Long goodsId);
}
