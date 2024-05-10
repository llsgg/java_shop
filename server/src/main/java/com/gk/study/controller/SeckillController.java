package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.Vo.GoodsVo;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Good;
import com.gk.study.entity.Order;
import com.gk.study.entity.SeckillOrder;
import com.gk.study.service.GoodsService;
import com.gk.study.service.OrderService;
import com.gk.study.service.ISeckillOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 秒杀
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/5/7 22:23
 */
@RestController
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private ISeckillOrderService ISeckillOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping( "/doSeckill")
    public String doSeckill()  {
        return "hello";
    }

    /**
     * @description:秒杀
     * Windos优化前QPS：936
     *       缓存QPS：1606
     *       优化QPS：2435
     * Linux优化前QPS：190.7
     * @author: longlin
     * @date: 2024/4/12 20:24
     * @param: [model, user, goodsId]
     * @return: [model, user, goodsId]
     **/
    @RequestMapping(value = "/doSeckill2", method = RequestMethod.POST)
    public APIResponse doSeckill2(@RequestParam("userId") Long userId, @RequestParam("goodsId") Long goodsId) {
        // 判断登录
        if (userId == null) return new APIResponse(ResponeCode.FAIL, "用户未登录", "");
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 用redis判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + userId + ":" + goodsId);
        // 重复抢购
        if (seckillOrder != null) {
            return new APIResponse(ResponeCode.FAIL, "重复秒杀", "");
        }

        // 预减库存，原子类型
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            valueOperations.increment("seckillGoods:" + goodsId);
            return new APIResponse(ResponeCode.FAIL, "库存不足", "");
        }
        GoodsVo good = goodsService.getGoodsVoById(goodsId);
        // 下单
        Order order = orderService.seckill(userId, good);
        return new APIResponse(ResponeCode.SUCCESS, "秒杀成功", order);
    }

    /**
     * @description:初始化
     * @author: longlin
     * @date: 2024/4/22 23:19
     * @param: []
     * @return: []
     **/
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.getGoodsVo();
        if (CollectionUtils.isEmpty(list)) return;
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
        });
    }
}
