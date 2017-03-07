package com.jay.mapper;

import com.jay.model.Order;

import java.util.List;

public interface OrderMapper {
	
	List<Order> getOrderListByUserId(Integer userId);
	
	Order getOrderByOrderId(Integer orderId);
	
	void createOrder(Order order);

}
