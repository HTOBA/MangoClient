package com.example.helper;

import java.util.HashMap;
import java.util.Map;

import android.app.DownloadManager;
import android.graphics.Bitmap;

// �������ڱ���customer���󣬱�������Ӧ�õ�ʹ��
public class PublicContainer {
	public static Customer customer = new Customer(0);
	public static Bitmap user_face;

	private static boolean has_user_info = false; // �Ƿ��Ѿ��ڷ���������û�����
	public static boolean has_init_addresses = false;
	public static boolean need_update_address = false;

	// ����Ϊprivate������ʵ����
	private PublicContainer() {
		// �յĹ��캯��
	}

	public static void setHasUserInfo(boolean bool) {
		has_user_info = bool;
	}

	public static boolean getHasUserInfo() {
		return has_user_info;
	}
}