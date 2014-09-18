/*
 * �ϴ�ͷ�񲿷��д����ơ����ü�ͼƬ������Ӧ��
 * ��ͼƬ��Ϊ��ʱ�ļ���������һ���ط��������ϴ�����������
 * �ϴ��ɹ�֮���ٰѸ�ͼƬ���ǵ����ص�ͷ��ͼƬ��
 * 
 * ���ڵ�ʵ���ǣ��Ѳü����ͼƬֱ���滻������ͷ��ͼƬ��
 * Ȼ��ſ�ʼ�ϴ�������������һ����������ϴ�ͼƬʱ�����쳣��
 * �򱾵���������ϵ����ݲ�ͳһ��
 * 
 * ��ʱ������picDirPath? isFolderExists���������󷵻�ִ�н����
 * */
package com.example.mangoclient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.helper.Customerlevel;
import com.example.helper.HttpDownloader;
import com.example.helper.Images;
import com.example.helper.MyDialog;
import com.example.helper.CircularImage;
import com.example.helper.PublicContainer;
import com.example.mangoclient.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyInfoActivity extends Activity implements OnClickListener {

	private Intent intent;
	private Button btn_address_info;
	private Button btn_order;
	private Button btn_logout;
	private Button btn_back_to_main;
	private Button btn_change_photo;

	CircularImage cover_user_photo; // Բ��ͷ��
	private String user_name; // �û���
	private String email_address; // ����
	private String phone_number;
	private String header_url; // ͷ��URL
	private TextView user_name_text;
	// private TextView email_text;
	// private TextView phone_number_text;

	// �Ի����е�������ť
	private Button btn_take_photo;
	private Button btn_choose_from_album;
	private Button btn_photo_edit_cancel;
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	private Uri imageUri;
	MyDialog dialog2;
	private static String picDirPath = "sdcard" + "/com.mangoclient/headpic/"; // �ֻ��ϴ洢ͷ���·��
	// private static String picName = "mango_face.jpg"; // ͷ���ļ���
	private static String picName;
	private File picFile;

	private boolean download_ok = false; // �Ƿ�ɹ����ص�ͼ��
	private boolean pass_ok = false; // �Ƿ��������̼߳���ִ��
	// �����߳�
	private MyInfoHandler handler = new MyInfoHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info);
		// // �����̣߳�����ͷ��
		// ChangeHeadThread chthread =new ChangeHeadThread();
		if (PublicContainer.getHasUserInfo() == false) {
			// ��ȡ�û���Ϣ
			GetUserInfoThread thread = new GetUserInfoThread();
			thread.start();
			while (!pass_ok) {
			}
			; // �ȴ�
			pass_ok = false;
		} else {
			user_name = PublicContainer.customer.getName();
			email_address = PublicContainer.customer.getEmail();
			phone_number = PublicContainer.customer.getMobile();
			header_url = PublicContainer.customer.getHead().getPath();
		}
		if (header_url.contains("http://")) {
			int pos = header_url.lastIndexOf("/");
			picName = header_url.substring(pos + 1);
		} else {
			picName = "default_head.jpg";
		}

		// �����û���Ϣ
		user_name_text = (TextView) findViewById(R.id.user_name_text);
		// email_text = (TextView) findViewById(R.id.email_text);
		// phone_number_text = (TextView) findViewById(R.id.phone_number_text);
		user_name_text.setText(user_name);
		// email_text.setText(email_address);
		// phone_number_text.setText(phone_number);

		// ����ͷ��
		// ����޷��ɹ�����Ŀ¼�����˳��
		if (isFolderExists(picDirPath)) {
			picFile = new File(picDirPath, picName);
		} else {
			Toast.makeText(this, "�ļ����޷�����", Toast.LENGTH_SHORT).show();
			finish();
		}
		imageUri = Uri.fromFile(picFile);
		cover_user_photo = (CircularImage) findViewById(R.id.cover_user_photo);

		if (picFile.exists() && picFile.length() > 0) {
			try {
				Bitmap face_bmp = BitmapFactory
						.decodeStream(getContentResolver().openInputStream(
								imageUri));
				cover_user_photo.setImageBitmap(face_bmp);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			DownloadThread dthread = new DownloadThread();
			dthread.start();
			while (pass_ok == false) {
			} // ѭ�����ȴ��߳̽���
			if (download_ok) {
				Bitmap face_bmp;
				try {
					face_bmp = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(imageUri));
					cover_user_photo.setImageBitmap(face_bmp);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				// �����������Ҳû��ͷ�񣬾�ʹ��Ĭ��ͷ��
				cover_user_photo.setImageResource(R.drawable.uface);
			}

		}

		btn_address_info = (Button) findViewById(R.id.btn_user_address);
		btn_address_info.setOnClickListener(this);

		btn_order = (Button) findViewById(R.id.btn_order);
		btn_order.setOnClickListener(this);

		btn_logout = (Button) findViewById(R.id.btn_logout);
		btn_logout.setOnClickListener(this);
		
		btn_back_to_main = (Button) findViewById(R.id.btn_back_to_main);
		btn_back_to_main.setOnClickListener(this);

		btn_change_photo = (Button) findViewById(R.id.btn_change_photo);
		btn_change_photo.setOnClickListener(this);

		dialog2 = new MyDialog(this, 180, 180, R.layout.dialog_layout,
				R.style.myDialogTheme);
		btn_take_photo = (Button) dialog2.findViewById(R.id.btn_take_photo);
		btn_choose_from_album = (Button) dialog2
				.findViewById(R.id.btn_choose_from_album);
		btn_photo_edit_cancel = (Button) dialog2
				.findViewById(R.id.btn_photo_edit_cancel);
		btn_take_photo.setOnClickListener(this);
		btn_choose_from_album.setOnClickListener(this);
		btn_photo_edit_cancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		File outputImage = picFile; // ͼƬ���·��
		// // �������ϵ��ļ�·��
		// String imagePath = PublicContainer.customer.getHead().getPath();
		// String imageName = PublicContainer.customer.getHead().getName();
		// File outputImage = new File(imagePath,imageName);

		switch (v.getId()) {
		case R.id.btn_back_to_main:
			finish();
			break;
		case R.id.btn_logout:
			PublicContainer.customer.setId(0); // ע����¼״̬
			PublicContainer.setHasUserInfo(false);
			finish();
			break;
		case R.id.btn_user_address:
			intent = new Intent(MyInfoActivity.this,
					AddressManageMainActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_order:
			intent = new Intent(MyInfoActivity.this, OrderActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_change_photo:
			dialog2.show();// ��ʾDialog
			break;
		case R.id.btn_photo_edit_cancel:
			if (dialog2.isShowing()) {
				dialog2.cancel();
			}
			break;
		case R.id.btn_take_photo:
			dialog2.cancel();
			try {
				if (outputImage.exists()) {
					outputImage.delete();
				}
				outputImage.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			// Intent intent = new Intent("andorid.media.action.IMAGE_CAPTURE");
			intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, TAKE_PHOTO);
			break;
		case R.id.btn_choose_from_album:
			dialog2.cancel();
			try {
				if (outputImage.exists()) {
					outputImage.delete();
				}
				outputImage.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			intent = new Intent("android.intent.action.GET_CONTENT");
			intent.setType("image/*");
			intent.putExtra("crop", true);
			intent.putExtra("scale", true);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			startActivityForResult(intent, TAKE_PHOTO);
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PHOTO:
			if (resultCode == RESULT_OK) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				Log.d("����ͼƬ�ɹ���", "" + picFile.exists());
				intent.setDataAndType(imageUri, "image/*");
				Log.d("MyInfoActivity", "imageUri = " + imageUri);
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent, CROP_PHOTO);
			}
			break;
		case CROP_PHOTO:
			if (resultCode == RESULT_OK) {
				try {
					Bitmap bitmap = BitmapFactory
							.decodeStream(getContentResolver().openInputStream(
									imageUri));
					cover_user_photo.setImageBitmap(bitmap);
					// ��ʼ�ϴ�ͷ��
					// TODO ȡ��ע�Ϳ��ϴ�ͷ�񵽷�����
					// UploadFile();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}

	// �ӷ���˻�ȡ�û�����
	class GetUserInfoThread extends Thread {

		@Override
		public void run() {
			Log.v("MyInfoActivity", "get user info thread runs");

			// ����һ��Post�����������������Ҫͨ��Post�������͵�����˵�����ͷ��������Ĳ��������øö�����
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_PERSON_URL);

			/* ���������������ʱʱ�� */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// ����sessionid����������session�ռ���Ч
			// if (sessionid != null) {
			// httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
			// }
			httpPost.setParams(httpParameters);

			// ����֤�룬�û�����������JSON��ʽ���͸������
			try {
				JSONObject params = new JSONObject();
				params.put("cusid", "" + PublicContainer.customer.getId());
				// params.put("cusid", "1");
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e1) {
				showMessage(e1.toString());
			} catch (JSONException e1) {
				showMessage("��Ϣ��ȡ����");
			}

			// ����һ���ͻ�����������
			HttpClient client = new DefaultHttpClient();

			// ����Get���󣬲��ȴ�����˵���Ӧ
			try {
				HttpResponse response = client.execute(httpPost);

				// ����������Ӧͷ��Ϣ
				// response.getAllHeaders();
				// ������Ӧ�壬����ʾ��Ӧͷ
				String responseBody = EntityUtils
						.toString(response.getEntity());
				Log.d("MyInfoActivity",
						"responsebody: " + responseBody.toString());

				JSONObject jsons = new JSONObject(responseBody);
				Log.d("MyInfoActivity", "just after jsonnnn");
				// ��֤������Ƿ����쳣
				if (responseBody.contains(",msg=")) {
					Log.d("MyInfoActivity", "contains ',msg='");
					showMessage(jsons.getString("msg"));
				} else {
					Log.d("MyInfoActivity",
							"response again: " + responseBody.toString());
					user_name = jsons.getString("name");
					email_address = jsons.getString("email");
					phone_number = jsons.getString("mobile");
					header_url = jsons.getString("head");
					PublicContainer.customer.setName(user_name);
					PublicContainer.customer.setEmail(email_address);
					PublicContainer.customer.setMobile(phone_number);

					Images img = new Images();
					img.setPath(header_url);
					PublicContainer.customer.setHead(img);

					PublicContainer.setHasUserInfo(true);

					Log.d("MyInfoActivity", "user_name=" + user_name
							+ "  email_address=" + email_address + "  phone="
							+ phone_number + "  user_header=" + header_url);
				}

			} catch (ClientProtocolException e) {
				// ����������֧�ֱ�׼HttpЭ�飬���������쳣
				showMessage(e.getMessage());
			} catch (IOException e) {
				// ����ͨѶ�������ϣ����������쳣
				showMessage("�޷�����Զ�̷�����");
			} catch (JSONException e) {
				// ������ص����ݸ�ʽ������JSON�ĸ�ʽ�����������쳣
				showMessage("���ز������󣬻�ȡ�û���Ϣʧ�ܣ�");
			} finally {
				pass_ok = true;
			}

		}
	}

	private void showMessage(String message) {
		Message msg = Message.obtain(handler, MyInfoHandler.SHOW_MESSAGE);
		msg.obj = message;
		msg.sendToTarget();
	}

	@SuppressLint("HandlerLeak")
	class MyInfoHandler extends Handler {
		// ��ʾ������Ϣ
		public static final int SHOW_MESSAGE = 1;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(MyInfoActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// �����ļ���ͷ��
	class DownloadThread extends Thread {
		// private String url = NetworkUtils.DANGDANG_BASE_URL + "upload/";
		private String url = header_url; // �����ļ���������URL

		@Override
		public void run() {
			// �п���url��Ч
			try {
				if (!url.startsWith("http://")) {
					throw (new Exception("url��Ч"));
				}
				HttpDownloader downloader = new HttpDownloader();
				String result = downloader.download(url,
						picDirPath.substring(7));
				if (result != null && result.length() > 0) {
					download_ok = true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				pass_ok = true;
				Log.d("MyInfoActivity", "downloader over!!!");
			}
		}
	}

	// �ж��ļ�Ŀ¼�Ƿ���ڣ��������򴴽�
	public boolean isFolderExists(String strFolder) {
		File file = new File(strFolder);
		if (!file.exists()) {
			if (file.mkdirs()) {
				return true;
			} else {
				return false;
			}
		}
		return true;

	}

	public void UploadFile() {
		String url = NetworkUtils.DANGDANG_HEADER_UPLOAD_URL;
		AsyncHttpClient client = new AsyncHttpClient();

		if (picFile.exists() && picFile.length() > 0) {
			RequestParams params = new RequestParams();
			try {
				params.put("profile_picture", picFile);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("haha", "before post");
			client.post(url, params, new AsyncHttpResponseHandler() {
				public void onSuccess(int statusCode, Header[] headers,
						byte[] responseBody) {
					Toast.makeText(MyInfoActivity.this, "�ϴ�ͷ��ɹ�",
							Toast.LENGTH_SHORT).show();

				}

				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(MyInfoActivity.this, "�ϴ�ͷ��ʧ��",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(MyInfoActivity.this, "�ļ�������", Toast.LENGTH_SHORT)
					.show();
		}
	}
}