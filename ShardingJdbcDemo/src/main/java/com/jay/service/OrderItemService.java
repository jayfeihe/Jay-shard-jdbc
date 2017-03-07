package com.jay.service;

import com.jay.mapper.OrderItemMapper;
import com.jay.model.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {
	
	@Autowired
	private OrderItemMapper orderItemMapper;
	
	public List<OrderItem> getOrderItemListByOrderId(Integer orderId) {
		return orderItemMapper.getOrderItemListByOrderId(orderId);
	}
	
	public void createOrderItem(OrderItem orderItem) {
		orderItemMapper.createOrderItem(orderItem);
	}

}
