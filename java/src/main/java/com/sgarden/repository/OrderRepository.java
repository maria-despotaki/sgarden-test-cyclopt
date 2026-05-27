package com.sgarden.repository;

import com.sgarden.model.Order;
import com.sgarden.model.OrderStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    List<Order> findByStatus(OrderStatus status);
}
