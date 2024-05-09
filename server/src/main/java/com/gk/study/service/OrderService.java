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

    List<Order> getUserOrderList(String userId, String status);
}
