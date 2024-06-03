package com.yx.search.receiver;


import com.rabbitmq.client.Channel;
import com.yx.mq.constant.MqConst;
import com.yx.search.service.SkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author 97557
 */
@Component
public class SkuReceiver {

    @Autowired
    private SkuService skuService;

    /**
     * 商品上架
     *
     * @param skuId   编号SKU ID
     * @param message 消息
     * @param channel 渠道
     * @throws IOException io异常
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_UPPER,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT),
            key = {MqConst.ROUTING_GOODS_UPPER}
    ))
    public void upperSku(Long skuId, Message message, Channel channel) throws IOException {
        if(skuId != null) {
            //调用方法商品上架
            skuService.upperSku(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    /**
     * 商品下架
     *
     * @param skuId   编号SKU ID
     * @param message 消息
     * @param channel 渠道
     * @throws IOException io异常
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_GOODS_LOWER,durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_GOODS_DIRECT),
            key = {MqConst.ROUTING_GOODS_LOWER}
    ))
    public void lowerSku(Long skuId, Message message, Channel channel) throws IOException {
        if(skuId != null) {
            skuService.lowerSku(skuId);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}
