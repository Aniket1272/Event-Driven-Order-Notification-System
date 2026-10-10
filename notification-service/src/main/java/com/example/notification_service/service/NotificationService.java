package com.example.notification_service.service;

import com.example.notification_service.model.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final RedisTemplate<String, String> redisTemplate;

    //In memory store for demo (Will use db for the actual usecase)
    private final List<Notification> notifications = new CopyOnWriteArrayList<>();

    private static final String REDIS_KEY_Prefix = "notification:processed:";

    public boolean isDuplicate(String orderId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_KEY_Prefix + orderId));
    }

    public void markAsProcessed(String orderId) {
        // Store in Redis with 24-hour TTL — idempotency key
        redisTemplate.opsForValue().set(REDIS_KEY_Prefix + orderId, "PROCESSED", 24, TimeUnit.HOURS);
        log.info("Marked orderId {} as processed in Redis", orderId);
    }

    public void save(Notification notification) {
        notifications.add(notification);
        log.info("Notification stored: {}", notification.getMessage());
    }

    public List<Notification> getAllNotifications() {
        return notifications;
    }
}
