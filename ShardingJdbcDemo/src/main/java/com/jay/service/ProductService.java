package com.jay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jay.mapper.ProductMapper;
import com.jay.model.Product;

@Service
public class ProductService {
	
	@Autowired
	private ProductMapper productMapper;
	
	public void createProduct(Product product) {
		productMapper.createProduct(product);
	}

}
