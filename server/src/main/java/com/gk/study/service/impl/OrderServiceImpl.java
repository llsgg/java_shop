package com.gk.study.service.impl;

import com.alibaba.druid.util.StringUtils;
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
import com.gk.study.utils.MD5Util;
import com.gk.study.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        order.setPrice(seckillGoods.getSeckillPrice());
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
    public boolean checkCaptcha(Long userId, Long goodsId, String captcha) {
        if (StringUtils.isEmpty(captcha)||null==userId||goodsId<0){
            return false;
        }
        if (!redisTemplate.hasKey("captcha:" + userId + ":" + goodsId)) return false;
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + userId + ":" + goodsId);
        return redisCaptcha.equals(captcha);
    }

    /**
     * @description:校验秒杀地址
     * @author: longlin
     * @date: 2024/4/24 22:59
     * @param: [user, goodsId, path]
     * @return: [user, goodsId, path]
     **/
    @Override
    public boolean checkPath(Long userId, Long goodsId, String path) {
        if (userId==null|| StringUtils.isEmpty(path)){
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + userId + ":" + goodsId);
        return path.equals(redisPath);
    }


    /**
     * @description:生成秒杀地址
     * @author: longlin
     * @date: 2024/4/24 22:59
     * @param: [user, goodsId]
     * @return: [user, goodsId]
     **/
    @Override
    public String createPath(Long userId, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + userId + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
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
