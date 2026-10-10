package com.example.order_service.kafka;

import com.example.order_service.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "order-events";

    public void sendOrderEvents(OrderEvent event) {
        log.info("Publishing OrderEvent for orderId: {}", event.getOrderId());
        kafkaTemplate.send(TOPIC, event.getOrderId(), event);
        log.info("OrderEvent published successfully for orderId: {}", event.getOrderId());
    }
}
