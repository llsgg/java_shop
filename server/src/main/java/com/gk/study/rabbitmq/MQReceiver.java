package com.gk.study.rabbitmq;

import com.gk.study.Vo.GoodsVo;
import com.gk.study.entity.SeckillMessage;
import com.gk.study.entity.SeckillOrder;
import com.gk.study.entity.User;
import com.gk.study.service.GoodsService;
import com.gk.study.service.OrderService;
import com.gk.study.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * 消息消费者
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/4/21 22:07
 */
@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * @description:下单操作
     * @author: longlin
     * @date: 2024/4/23 0:15
     * @param: []
     * @return: []
     **/
    @RabbitListener(queues = "seckillQueue")
    public void receive(String message) {
//        log.info("接受的消息:" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodsId();
//        User user = seckillMessage.getUser();
        Long userId = seckillMessage.getUserId();
        GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
        if (goodsVo.getStockCount() < 1) {
            return;
        }
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + userId + ":" + goodsId);
        if (seckillOrder != null) {
            return;
        }
        // 下单操作
        orderService.seckill(userId, goodsVo);

    }

}
