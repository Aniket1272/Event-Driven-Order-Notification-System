package com.example.order_service.service;

import com.example.order_service.event.OrderEvent;
import com.example.order_service.kafka.OrderProducer;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    @Autowired
    private final OrderProducer orderProducer;
    private final OrderRepository orderRepository;

    public Order createOrder(Order order) {
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        //Save order to mongodb
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getId());

        //Publish event to kafka
        OrderEvent event = new OrderEvent(
                savedOrder.getId(),
                savedOrder.getProductName(),
                savedOrder.getQuanitity(),
                savedOrder.getPrice(),
                savedOrder.getStatus()
        );
        orderProducer.sendOrderEvents(event);

        return savedOrder;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
    }
}
