package com.example.helper;

import java.util.HashMap;

public class ShoppingCart {

	private String cusName = null;
	private HashMap<String, Integer> data = null;

	public ShoppingCart(String cusName) {
		this.cusName = cusName;
	}

	public String getCusName() {
		return cusName;
	}

	public void setCusName(String cusName) {
		this.cusName = cusName;
	}

	public HashMap<String, Integer> getData() {
		return data;
	}

	public void setData(HashMap<String, Integer> data) {
		this.data = data;
	}

}
