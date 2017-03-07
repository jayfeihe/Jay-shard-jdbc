package com.jay.mapper;

import com.jay.model.City;
import com.jay.model.Product;

public interface ProductMapper {
	
	public void createProduct(Product product);
	
	public City getProductByProductId(Integer productId);

}
