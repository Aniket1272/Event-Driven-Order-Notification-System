package com.example.notification_service.kafka;

import com.example.notification_service.event.OrderEvent;
import com.example.notification_service.model.Notification;
import com.example.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group",
            containerFactory = "kafkaListenerContainerFactory")
    public void consumeOrderEvent(OrderEvent event) {
        log.info("Received Kafka event for orderId: {}", event.getOrderId());

        //Redis idempotency check - skip if already processed
        if (notificationService.isDuplicate(event.getOrderId())) {
            log.warn("Duplicate event received for orderId: {}. Skipping...", event.getOrderId());
            return;
        }

        Notification notification = Notification.builder()
                .orderId(event.getOrderId())
                .productName(event.getProductName())
                .message("Order " + event.getStatus() + " for product: " + event.getProductName()
                        + "at " + event.getPrice() + "Order ID: " + event.getOrderId())
                .processedAt(java.time.LocalDateTime.now())
                .build();

        notificationService.save(notification);
        notificationService.markAsProcessed(event.getOrderId());

        log.info("\"Notification processed for orderId: {}", event.getOrderId());
    }
}
