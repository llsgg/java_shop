package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.Vo.GoodsVo;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.*;
import com.gk.study.rabbitmq.MQSender;
import com.gk.study.service.GoodsService;
import com.gk.study.service.OrderService;
import com.gk.study.service.ISeckillOrderService;
import com.gk.study.service.UserService;
import com.gk.study.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript script;
    @Autowired
    private MQSender mqSender;
    private Map<Long, Boolean> EmptyStockMap = new HashMap<>(); // 做标记，某个商品没有了就放入map
    @Autowired
    private UserService userService;

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
    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    public APIResponse doSeckill(Long goodsId, @CookieValue("userTicket") String ticket) {
        // 判断登录
        if (ticket == null) return new APIResponse(ResponeCode.FAIL, "用户未登录", "");
//        System.out.println("ticket: " + ticket);
        User user = (User)redisTemplate.opsForValue().get("user:" + ticket);
        if (user == null) return new APIResponse(ResponeCode.FAIL, "用户未登录", "");
        Long userId = user.getId();


        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 用redis判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + userId + ":" + goodsId);

        // 重复抢购
        if (seckillOrder != null) {
            return new APIResponse(ResponeCode.FAIL, "重复秒杀", "");
        }

//        // 通过内存标记，减少redis访问
//        if (EmptyStockMap.get(goodsId)) {
//            return new APIResponse(ResponeCode.FAIL, "库存不足", "");
//        }

        // 预减库存，原子类型
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if (stock < 0) {
//            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return new APIResponse(ResponeCode.FAIL, "库存不足", "");
        }
        // 下单
        GoodsVo good = goodsService.getGoodsVoById(goodsId);
        Order order = orderService.seckill(userId, good);
        return new APIResponse(ResponeCode.SUCCESS, "秒杀成功", order);

//        return new APIResponse(ResponeCode.SUCCESS, "0", 0); // 0代表排队中
    }

    /**
     * @description:获取秒杀结果
     * @author: longlin
     * @date: 2024/4/23 11:17
     * @param: [user, goodsId]
     * @return: [user, goodsId] orderId:成功、-1：失败、0：派对中
     **/
    @RequestMapping("/result")
    @ResponseBody
    public APIResponse getResult(Long userId, Long goodsId) {
        if (userId == null) return new APIResponse(ResponeCode.FAIL, "用户未登录", "");
        Long orderId = seckillOrderService.getResult(userId, goodsId);
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", 1); // 0代表排队中
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
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
