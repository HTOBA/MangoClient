/*
 * 上传头像部分有待完善。当裁剪图片结束后，应该
 * 把图片作为临时文件保存在另一个地方，尝试上传到服务器。
 * 上传成功之后，再把该图片覆盖掉本地的头像图片。
 * 
 * 现在的实现是：把裁剪后的图片直接替换掉本地头像图片，
 * 然后才开始上传服务器。这样一来，如果在上传图片时出现异常，
 * 则本地与服务器上的数据不统一。
 * 
 * 何时创建的picDirPath? isFolderExists函数创建后返回执行结果。
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

	CircularImage cover_user_photo; // 圆形头像
	private String user_name; // 用户名
	private String email_address; // 邮箱
	private String phone_number;
	private String header_url; // 头像URL
	private TextView user_name_text;
	// private TextView email_text;
	// private TextView phone_number_text;

	// 对话框中的三个按钮
	private Button btn_take_photo;
	private Button btn_choose_from_album;
	private Button btn_photo_edit_cancel;
	public static final int TAKE_PHOTO = 1;
	public static final int CROP_PHOTO = 2;
	private Uri imageUri;
	MyDialog dialog2;
	private static String picDirPath = "sdcard" + "/com.mangoclient/headpic/"; // 手机上存储头像的路径
	// private static String picName = "mango_face.jpg"; // 头像文件名
	private static String picName;
	private File picFile;

	private boolean download_ok = false; // 是否成功下载到图像
	private boolean pass_ok = false; // 是否允许主线程继续执行
	// 用于线程
	private MyInfoHandler handler = new MyInfoHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_info);
		// // 启动线程，更新头像
		// ChangeHeadThread chthread =new ChangeHeadThread();
		if (PublicContainer.getHasUserInfo() == false) {
			// 获取用户信息
			GetUserInfoThread thread = new GetUserInfoThread();
			thread.start();
			while (!pass_ok) {
			}
			; // 等待
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

		// 设置用户信息
		user_name_text = (TextView) findViewById(R.id.user_name_text);
		// email_text = (TextView) findViewById(R.id.email_text);
		// phone_number_text = (TextView) findViewById(R.id.phone_number_text);
		user_name_text.setText(user_name);
		// email_text.setText(email_address);
		// phone_number_text.setText(phone_number);

		// 加载头像
		// 如果无法成功创建目录，则退出活动
		if (isFolderExists(picDirPath)) {
			picFile = new File(picDirPath, picName);
		} else {
			Toast.makeText(this, "文件夹无法创建", Toast.LENGTH_SHORT).show();
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
			} // 循环，等待线程结束
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
				// 如果服务器上也没有头像，就使用默认头像
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

		File outputImage = picFile; // 图片存放路径
		// // 服务器上的文件路径
		// String imagePath = PublicContainer.customer.getHead().getPath();
		// String imageName = PublicContainer.customer.getHead().getName();
		// File outputImage = new File(imagePath,imageName);

		switch (v.getId()) {
		case R.id.btn_back_to_main:
			finish();
			break;
		case R.id.btn_logout:
			PublicContainer.customer.setId(0); // 注销登录状态
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
			dialog2.show();// 显示Dialog
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
				Log.d("创建图片成功？", "" + picFile.exists());
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
					// 开始上传头像
					// TODO 取消注释可上传头像到服务器
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

	// 从服务端获取用户数据
	class GetUserInfoThread extends Thread {

		@Override
		public void run() {
			Log.v("MyInfoActivity", "get user info thread runs");

			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_PERSON_URL);

			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效
			// if (sessionid != null) {
			// httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
			// }
			httpPost.setParams(httpParameters);

			// 讲验证码，用户名和密码以JSON格式发送给服务端
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
				showMessage("信息获取错误！");
			}

			// 创建一个客户端请求发送器
			HttpClient client = new DefaultHttpClient();

			// 发送Get请求，并等待服务端的响应
			try {
				HttpResponse response = client.execute(httpPost);

				// 返回所有响应头信息
				// response.getAllHeaders();
				// 返回响应体，不显示响应头
				String responseBody = EntityUtils
						.toString(response.getEntity());
				Log.d("MyInfoActivity",
						"responsebody: " + responseBody.toString());

				JSONObject jsons = new JSONObject(responseBody);
				Log.d("MyInfoActivity", "just after jsonnnn");
				// 验证服务端是否有异常
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
				// 服务端如果不支持标准Http协议，则出现这个异常
				showMessage(e.getMessage());
			} catch (IOException e) {
				// 网络通讯发生故障，则出现这个异常
				showMessage("无法连接远程服务器");
			} catch (JSONException e) {
				// 如果返回的数据格式不满足JSON的格式，则出现这个异常
				showMessage("返回参数错误，获取用户信息失败！");
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
		// 显示错误信息
		public static final int SHOW_MESSAGE = 1;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(MyInfoActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	// 下载文件（头像）
	class DownloadThread extends Thread {
		// private String url = NetworkUtils.DANGDANG_BASE_URL + "upload/";
		private String url = header_url; // 包含文件名的完整URL

		@Override
		public void run() {
			// 有可能url无效
			try {
				if (!url.startsWith("http://")) {
					throw (new Exception("url无效"));
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

	// 判断文件目录是否存在，不存在则创建
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
					Toast.makeText(MyInfoActivity.this, "上传头像成功",
							Toast.LENGTH_SHORT).show();

				}

				public void onFailure(int statusCode, Header[] headers,
						byte[] responseBody, Throwable error) {
					Toast.makeText(MyInfoActivity.this, "上传头像失败",
							Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(MyInfoActivity.this, "文件不存在", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
