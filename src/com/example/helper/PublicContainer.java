package com.example.helper;

import java.util.HashMap;
import java.util.Map;

import android.app.DownloadManager;
import android.graphics.Bitmap;

// 此类用于保存customer对象，便于整个应用的使用
public class PublicContainer {
	public static Customer customer = new Customer(0);
	public static Bitmap user_face;

	private static boolean has_user_info = false; // 是否已经在服务器获得用户资料
	public static boolean has_init_addresses = false;
	public static boolean need_update_address = false;

	// 设置为private，不可实例化
	private PublicContainer() {
		// 空的构造函数
	}

	public static void setHasUserInfo(boolean bool) {
		has_user_info = bool;
	}

	public static boolean getHasUserInfo() {
		return has_user_info;
	}
}
