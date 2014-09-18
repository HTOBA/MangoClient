package com.example.helper;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class HttpDownloader {

	private URL url = null;

	/**
	 * ����URL�����ļ�,ǰ��������ļ����е��������ı�,�����ķ���ֵ�����ı����е����� 1.����һ��URL����
	 * 2.ͨ��URL����,����һ��HttpURLConnection���� 3.�õ�InputStream 4.��InputStream���ж�ȡ����
	 * 
	 * @param urlStr
	 *            :�����ļ���ַ
	 * @param path
	 *            :ָ�����ص�SD���ϵ��ļ�Ŀ¼
	 * @return ���浽SD�����ļ�·��
	 */
	public String download(String urlStr, String path) {

		int start = urlStr.lastIndexOf("/");
		int end = urlStr.length();
		String fileName = urlStr.substring(start, end);// ��ȡ�ļ�����Ϊ���ص�SD���ϵ��ļ���

		HttpURLConnection urlConn = null;
		try {
			url = new URL(urlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.connect();// һ��Ҫ���ϣ�����urlConn.getInputStream()����
			urlConn.setConnectTimeout(6000);
			InputStream inputStream = urlConn.getInputStream();
			FileUtil fileUtils = new FileUtil();
			File resultFile = fileUtils.write2SDFromInput(path, fileName,
					inputStream);

			if (resultFile == null) {
				return null;
			}
			return resultFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != urlConn)
				urlConn.disconnect();
		}
		return null;
	}
}