package com.jay.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jay.id.ProductIdGenerator;
import com.jay.model.Product;
import com.jay.service.ProductService;

@RestController
@RequestMapping("/product")
public class ProductController {
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductIdGenerator productIdGenerator;
	
	@RequestMapping(path="/{productName}", method={RequestMethod.POST})
	public String createProduct(@PathVariable("productName") String productName) {
		Product product = new Product();
		product.setProductId(productIdGenerator.generateId().intValue());
		product.setProductName(productName);
		productService.createProduct(product);
		return "success";
	}

}
