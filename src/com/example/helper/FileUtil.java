package com.example.helper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.os.Environment;
import android.util.Log;

/**
 * <p>
 * Title: FileUtil.java
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author <a href="mailto:tanglei3shi@163.com">Administrator</a>
 * @date 2011-3-3 ����03:28:05
 * @version 1.0
 */
public class FileUtil {

	private String SDPATH;

	private int FILESIZE = 4 * 1024;

	public String getSDPATH() {
		return SDPATH;
	}

	public FileUtil() {
		// �õ���ǰ�ⲿ�洢�豸��Ŀ¼( /SDCARD )
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}

	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public File createSDFile(String fileName) throws IOException {
		File file = new File(SDPATH + fileName);
		file.createNewFile();
		return file;
	}

	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 * @return
	 */
	public File createSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isFileExist(String fileName) {
		File file = new File(SDPATH + fileName);
		return file.exists();
	}

	/**
	 * ��һ��InputStream���������д�뵽SD����
	 * 
	 * @param path
	 * @param fileName
	 * @param input
	 * @return
	 */
	public File write2SDFromInput(String path, String fileName,
			InputStream input) {

		File file = null;
		OutputStream output = null;
		try {
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte[] buffer = new byte[FILESIZE];
			while ((input.read(buffer)) != -1) {
				output.write(buffer);
			}
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != output) {
					output.close();
				}
				if (null != input) {
					input.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return file;
	}

	/**
	 * ֱ��ͨ��HTTPЭ���ύ���ݵ�������,ʵ�ֱ��ύ����
	 * 
	 * @param actionUrl
	 *            �ϴ�·��
	 * @param params
	 *            ������� keyΪ������,valueΪ����ֵ
	 * @param file
	 *            �ϴ��ļ�
	 */
	@SuppressWarnings("unused")
	public static String post(String actionUrl, Map<String, String> params,
			FormFile[] files) {
		try {
			String BOUNDARY = "---------7d4a6d158c9"; // ���ݷָ���
			String MULTIPART_FORM_DATA = "multipart/form-data";

			URL url = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(6000);
			conn.setDoInput(true);// ��������
			conn.setDoOutput(true);// �������
			conn.setUseCaches(false);// ��ʹ��Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);

			StringBuilder sb = new StringBuilder();

			// �ϴ��ı��������֣���ʽ��ο�����
			for (Map.Entry<String, String> entry : params.entrySet()) {// �������ֶ�����
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}

			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// ���ͱ��ֶ�����

			FileInputStream fis = null;
			// �ϴ����ļ����֣���ʽ��ο�����
			for (FormFile file : files) {
				StringBuilder split = new StringBuilder();
				split.append("--");
				split.append(BOUNDARY);
				split.append("\r\n");
				split.append("Content-Disposition: form-data;name=\""
						+ file.getFormname() + "\";filename=\""
						+ file.getFilname() + "\"\r\n");
				split.append("Content-Type: " + file.getContentType()
						+ "\r\n\r\n");
				outStream.write(split.toString().getBytes());
				outStream.write(file.getData(), 0, file.getData().length);
				outStream.write("\r\n".getBytes());
			}
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// ���ݽ�����־
			outStream.write(end_data);
			outStream.flush();

			int cah = conn.getResponseCode();
			Log.i("userinfo", "" + cah);

			if (cah != 200)
				throw new RuntimeException("����urlʧ��");
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "utf-8");// ����������⣬��֤�������˷�������������ʾ
			BufferedReader br = new BufferedReader(isr);
			String result = br.readLine();
			outStream.close();
			conn.disconnect();
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ��ȡԴ�ļ�����
	 * 
	 * @param filename
	 *            String �ļ�·��
	 * @throws IOException
	 * @return byte[] �ļ�����
	 */
	public static byte[] readFile(String filename) {
		File file = new File(filename);
		long len = file.length();
		Log.i("userinfo", "file.length():" + len);
		byte[] bytes = new byte[(int) len];

		BufferedInputStream bufferedInputStream;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(
					file));
			int r = bufferedInputStream.read(bytes);
			if (r != len) {
				bufferedInputStream.close();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bytes;

	}

}
