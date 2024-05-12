package com.gk.study.service;


import com.gk.study.entity.Good;
import com.gk.study.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getOrderList();
    void createOrder(Order order);
    void deleteOrder(String id);

    void updateOrder(Order order);

    Order seckill(Long userId, Good good);

    boolean checkCaptcha(Long userId, Long goodsId, String captcha);

    /**
     * @description: 检查秒杀地址
     * @author: longlin
     * @date: 2024/5/11 13:44
     * @param: [userId, goodsId, path]
     * @return: [userId, goodsId, path]
     **/
    boolean checkPath(Long userId, Long goodsId, String path);

    /**
     * @description:获取秒杀地址
     * @author: longlin
     * @date: 2024/4/24 23:07
     * @param: [user, goodsId]
     * @return: [user, goodsId]
     **/
    String createPath(Long userId, Long goodsId);

    List<Order> getUserOrderList(String userId, String status);
}
