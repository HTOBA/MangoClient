package com.example.mangoclient;

public class NetworkUtils {
	public static String DANGDANG_BASE_URL = "http://172.27.35.1/MangGoServer/";

	// 获取验证码的URL
	public static String DANGDANG_CODE_URL = DANGDANG_BASE_URL
	// 真实环境URL
	// + "code.jhtml";

			// 测试用的URL
			+ "image.json";
	// 实施登陆验证的URL
	public static String DANGDANG_LOGIN_URL = DANGDANG_BASE_URL + "login.json";
	public static String DANGDANG_BOOKS_URL = DANGDANG_BASE_URL + "books.json";
	public static String DANGDANG_REGISTER_URL = DANGDANG_BASE_URL
			+ "register.json";
	public static String DANGDANG_toppick_URL = DANGDANG_BASE_URL
			+ "toppick.json";
	public static String DANGDANG_PERSON_URL = DANGDANG_BASE_URL
			+ "personalinfo.json";
	public static String DANGDANG_ALL_ADDRESS_URL = DANGDANG_BASE_URL
			+ "alladdress.json";
	public static String DANGDANG_ADDRESS_CHANGE_URL = DANGDANG_BASE_URL
			+ "addresschange.json";
	public static String DANGDANG_SEARCH_URL = DANGDANG_BASE_URL
			+ "search.json";
	public static String DANGDANG_ORDER_URL = DANGDANG_BASE_URL
			+ "orderslist.json";
	public static String DANGDANG_SHOPPING_URL = DANGDANG_BASE_URL
			+ "cart.json";
	public static String DANGDANG_COMMENTCOMMIT_URL = DANGDANG_BASE_URL
			+ "commentcommit.json";

	public static String DANGDANG_ORDERCONFIRM_URL = DANGDANG_BASE_URL
			+ "orderconfirm.json";

	public static String DANGDANG_BOOKINFO_URL = DANGDANG_BASE_URL
			+ "bookinfo.json";
	public static String DANGDANG_HEADER_UPLOAD_URL = DANGDANG_BASE_URL
			+ "headerupload.json";
	public static String DANGDANG_COMMENTS_URL = DANGDANG_BASE_URL
			+ "commentlist.json";
}
