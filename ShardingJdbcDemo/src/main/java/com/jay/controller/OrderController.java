package com.jay.controller;

import com.jay.id.OrderIdGenerator;
import com.jay.id.OrderItemIdGenerator;
import com.jay.model.Order;
import com.jay.model.OrderItem;
import com.jay.service.OrderItemService;
import com.jay.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderItemService orderItemService;
	
	@Autowired
	private OrderIdGenerator orderIdGenerator;
	
	@Autowired
	private OrderItemIdGenerator orderItemIdGenerator;
	
	@RequestMapping(path="/{userId}", method={RequestMethod.GET})
	public List<Order> getOrderListByUserId(@PathVariable("userId") Integer userId) {
		return orderService.getOrderListByUserId(userId);
	}
	
	@RequestMapping(path="/oid/{orderId}", method={RequestMethod.GET})
	public Order getOrderByOrderId(@PathVariable("orderId") Integer orderId) {
		return orderService.getOrderByOrderId(orderId);
	}
	
	@RequestMapping(method={RequestMethod.POST})
	public String createOrder(@RequestBody Order order) {
		int orderId = orderIdGenerator.generateId().intValue();
		order.setOrderId(orderId);
		orderService.createOrder(order);
		for (OrderItem oi : order.getOrderItemList()) {
			oi.setItemId(orderItemIdGenerator.generateId().intValue());
			oi.setOrderId(orderId);
			orderItemService.createOrderItem(oi);
		}
		
		return "success";
	}

}
