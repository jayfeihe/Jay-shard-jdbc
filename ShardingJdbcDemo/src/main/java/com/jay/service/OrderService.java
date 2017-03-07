package com.jay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jay.mapper.OrderMapper;
import com.jay.model.Order;

@Service
public class OrderService {

	@Autowired
	private OrderMapper orderMapper;

	public Order getOrderByOrderId(Integer orderId) {
		Order order = orderMapper.getOrderByOrderId(orderId);
		return order;
	}

	public List<Order> getOrderListByUserId(Integer userId) {
		List<Order> orderList = orderMapper.getOrderListByUserId(userId);
		return orderList;
	}

	public void createOrder(Order order) {
		orderMapper.createOrder(order);
	}

}
