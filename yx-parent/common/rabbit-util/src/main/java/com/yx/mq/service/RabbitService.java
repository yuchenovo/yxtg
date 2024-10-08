package com.yx.mq.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送短消息
     *
     * @param exchange   交换机
     * @param routingKey 路由
     * @param message    消息
     * @return boolean
     */
    public boolean sendMessage(String exchange,String routingKey,Object message) {
        rabbitTemplate.convertAndSend(exchange,routingKey,message);
        return true;
    }

}
