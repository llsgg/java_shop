package com.gk.study.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.Vo.GoodsVo;
import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Good;
import com.gk.study.entity.Order;
import com.gk.study.entity.SeckillMessage;
import com.gk.study.entity.SeckillOrder;
import com.gk.study.rabbitmq.MQSender;
import com.gk.study.service.GoodsService;
import com.gk.study.service.OrderService;
import com.gk.study.service.ISeckillOrderService;
import com.gk.study.utils.JsonUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 秒杀
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/5/7 22:23
 */
@Controller
@RequestMapping("/seckill")
@Slf4j
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

    /**
     * @description:获取秒杀地址
     * @author: longlin
     * @date: 2024/4/24 23:03
     * @param: [user, goodsId]
     * @return: [user, goodsId]
     **/
    @RequestMapping(value = "/path", method = RequestMethod .GET)
    @ResponseBody
    public APIResponse getPath(Long userId, Long goodsId, String captcha) {
        if (userId == null) return new APIResponse(ResponeCode.FAIL, "用户未登录", "");
        boolean check = orderService.checkCaptcha(userId, goodsId, captcha);
        if (!check){
            return new APIResponse(ResponeCode.FAIL, "验证码已过期", "");
        }
        String str = orderService.createPath(userId,goodsId);
        return new APIResponse(ResponeCode.SUCCESS, "", str);
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
    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.GET)
    @ResponseBody
    public APIResponse doSeckill(@PathVariable String path, Long userId, Long goodsId) {
        // 判断登录
        if (userId == null) return new APIResponse(ResponeCode.FAIL, "用户未登录", "");
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // 判断路径
        boolean check = orderService.checkPath(userId, goodsId, path);
        if (!check) {
            return new APIResponse(ResponeCode.FAIL, "请求非法", "");
        }

        // 用redis判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + userId + ":" + goodsId);

        // 重复抢购
        if (seckillOrder != null) {
            return new APIResponse(ResponeCode.FAIL, "重复秒杀", "");
        }

        // 通过内存标记，减少redis访问
        if (EmptyStockMap.get(goodsId)) {
            return new APIResponse(ResponeCode.FAIL, "库存不足", "");
        }

        // 预减库存，原子类型
//        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        Long stock = (Long) redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return new APIResponse(ResponeCode.FAIL, "库存不足", "");
        }
        // 下单
        SeckillMessage message = new SeckillMessage(userId, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));

        return new APIResponse(ResponeCode.SUCCESS, "0", 0); // 0代表排队中
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
     * @description:验证码
     * @author: longlin
     * @date: 2024/4/25 0:22
     * @param: [user, goodsId, response]
     * @return: [user, goodsId, response]
     **/
    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public APIResponse verifyCode(Long userId, Long goodsId, HttpServletResponse response) {
        if (null==userId||goodsId<0){
            return new APIResponse(ResponeCode.FAIL, "非法请求", "");
        }
//        log.info("userId:"+userId+"goodsId:"+goodsId);
//        System.out.println("userId = " + userId);
        // 设置请求头为输出图片类型
        response.setContentType("image/jpg");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control" , "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);

        redisTemplate.opsForValue().set("captcha:"+userId+":"+goodsId,captcha.text (),60, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败",e.getMessage());
        }
        return new APIResponse(ResponeCode.SUCCESS, "获取成功", "");
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
