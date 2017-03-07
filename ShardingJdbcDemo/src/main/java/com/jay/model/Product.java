package com.jay.model;

import java.io.Serializable;

public class Product implements Serializable {
	
	public Integer productId;
	
	public String productName;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
