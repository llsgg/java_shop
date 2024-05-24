package com.gk.study.rabbitmq;

import com.gk.study.Vo.GoodsVo;
import com.gk.study.entity.SeckillMessage;
import com.gk.study.entity.SeckillOrder;
import com.gk.study.entity.User;
import com.gk.study.service.GoodsService;
import com.gk.study.service.OrderService;
import com.gk.study.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.nio.channels.Channel;

/**
 * 消息消费者
 *
 * @author longlin
 * @version 1.0
 * Create by 2024/4/21 22:07
 */
@Service
@Slf4j
public class MQReceiver implements ChannelAwareMessageListener {
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

//    public void receive(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
//        try {
//            log.info("接受的消息:" + message);
//            SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
//            Long goodsId = seckillMessage.getGoodsId();
//            Long userId = seckillMessage.getUserId();
//            GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
//            if (goodsVo.getStockCount() < 1) {
//                // 拒绝消息
//                channel.basicReject(tag, false);
//                return;
//            }
//            SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + userId + ":" + goodsId);
//            if (seckillOrder != null) {
//                // 拒绝消息
//                channel.basicReject(tag, false);
//                return;
//            }
//            // 下单操作
//            orderService.seckill(userId, goodsVo);
//            // 确认消息
//            channel.basicAck(tag, false);
//        } catch (Exception e) {
//            // 拒绝消息
//            try {
//                channel.basicReject(tag, false);
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//    }


    @RabbitListener(queues = "seckillQueue")
    public void onMessage(Message message, com.rabbitmq.client.Channel channel) throws Exception {
        try {
            String messageBody = new String(message.getBody());
            log.info("接受的消息:" + messageBody);
            SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(messageBody, SeckillMessage.class);
            Long goodsId = seckillMessage.getGoodsId();
            Long userId = seckillMessage.getUserId();
            GoodsVo goodsVo = goodsService.getGoodsVoById(goodsId);
            if (goodsVo.getStockCount() < 1) {
                // 拒绝消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + userId + ":" + goodsId);
            if (seckillOrder != null) {
                // 拒绝消息
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                return;
            }
            // 下单操作
            orderService.seckill(userId, goodsVo);
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 拒绝消息
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
