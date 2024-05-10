package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Good;
import com.gk.study.entity.Order;
import com.gk.study.entity.SeckillGoods;
import com.gk.study.entity.SeckillOrder;
import com.gk.study.service.ISeckillGoodsService;
import com.gk.study.service.ISeckillOrderService;
import com.gk.study.service.OrderService;
import com.gk.study.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ISeckillGoodsService seckillGoodsService;
    @Autowired
    ISeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Order> getOrderList() {
        return orderMapper.getList();
    }

    @Override
    public void createOrder(Order order) {
        long ct = System.currentTimeMillis();
//        order.setOrderTime(String.valueOf(ct));
        order.setOrderTime(new Date());
        order.setOrderNumber(String.valueOf(ct));
        order.setStatus(1);
        orderMapper.insert(order);
    }

    @Override
    public void deleteOrder(String id) {
        orderMapper.deleteById(id);
    }

    @Override
    public void updateOrder(Order order) {
        orderMapper.updateById(order);
    }

    @Transactional
    @Override
    public Order seckill(Long userId, Good good) {
        // 秒杀商品表减库存
        SeckillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", good.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").eq("id", seckillGoods.getId()).gt("stock_count", 0));
        if (!result) return null;

        //生成订单
        Order order = new Order();
        order.setUserId(userId);
        order.setGoodId(good.getId());
        order.setTitle(good.getTitle());
        order.setCount(1);
//        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setStatus(0);
        order.setOrderTime(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SeckillOrder tSeckillOrder = new SeckillOrder();
        tSeckillOrder.setUserId(userId);
        tSeckillOrder.setOrderId(order.getId());
        tSeckillOrder.setGoodsId(good.getId());
        seckillOrderService.save(tSeckillOrder);
        redisTemplate.opsForValue().set("order:" + userId + ":" + good.getId(), tSeckillOrder);
        return order;
    }

    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return orderMapper.getUserOrderList(userId, status);
//        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", userId);
//        if (StringUtils.isNotBlank(status)) {
//            queryWrapper.eq("status", status);
//        }
//        queryWrapper.orderBy(true, false, "order_time");
//        return mapper.selectList(queryWrapper);
    }
}
