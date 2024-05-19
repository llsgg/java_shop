package com.gk.study.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 消息发送者
 * @author longlin
 * @version 1.0
 * Create by 2024/4/21 22:03
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * @description:发送秒杀信息
     * @author: longlin
     * @date: 2024/4/22 23:41
     * @param:
     * @return:
     **/
    public void sendSeckillMessage(String message) {
//        log.info("发送秒杀信息" + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }


//
//    public void send(Object msg) {
//        log.info("发送消息:" + msg);
//        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
//    }
//
//    public void send01(Object msg) {
//        log.info("发送red消息：" + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.red", "hello red");
//    }
//    public void send02(Object msg) {
//        log.info("发送green消息：" + msg);
//        rabbitTemplate.convertAndSend("directExchange", "queue.green", "hello_green");
//    }
//
//    public void send03(Object msg) {
//        log.info("发送topic消息" + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "queue.red.massage", msg);
//    }
//    public void send04(Object msg) {
//        log.info("发送消息（两个queue接受）：" + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "massage.queue.green.abc", msg);
//    }
}
