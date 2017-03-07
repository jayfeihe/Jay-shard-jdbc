package com.jay.mapper;

import com.jay.model.OrderItem;

import java.util.List;

public interface OrderItemMapper {
	
	List<OrderItem> getOrderItemListByOrderId(Integer orderId);
	
	void createOrderItem(OrderItem orderItem);

}
