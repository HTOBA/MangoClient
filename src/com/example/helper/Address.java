package com.example.helper;

public class Address {
	private int address_id; // 用于区分每一个address
	private String name; // 收货人姓名
	private String country;
	private String province;
	private String city;
	private String district;
	private String detail_address; // 街道地址
	private String postcode;
	private String phone_number;

	public Address(int address_id, String name, String country,
			String province, String city, String district,
			String detail_address, String postcode, String phone_number) {
		setAddressId(address_id);
		setName(name);
		setCountry(country);
		setProvince(province);
		setCity(city);
		setDistrict(district);
		setDetailAddress(detail_address);
		setPostcode(postcode);
		setPhoneNumber(phone_number);
	}

	public Address(String name, String country, String province, String city,
			String district, String detail_address, String postcode,
			String phone_number) {
		setAddressId(0); // 默认id为0
		setName(name);
		setCountry(country);
		setProvince(province);
		setCity(city);
		setDistrict(district);
		setDetailAddress(detail_address);
		setPostcode(postcode);
		setPhoneNumber(phone_number);
	}

	// 获取完整收货地址
	public String getWholeAddress() {
		String wa;
		if (getProvince().equals(getCity())) {
			wa = getCountry() + getCity() + getDistrict() + getDetailAddress();
		} else {
			wa = getCountry() + getProvince() + getCity() + getDistrict()
					+ getDetailAddress();
		}
		return wa;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDetailAddress() {
		return detail_address;
	}

	public void setDetailAddress(String detail_address) {
		this.detail_address = detail_address;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getPhoneNumber() {
		return phone_number;
	}

	public void setPhoneNumber(String phone_number) {
		this.phone_number = phone_number;
	}

	public int getAddressId() {
		return address_id;
	}

	public void setAddressId(int address_id) {
		this.address_id = address_id;
	}
}
