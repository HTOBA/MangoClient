package com.example.mangoclient;

import java.io.IOException;
import java.io.InputStream;
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
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.helper.PublicContainer;
import com.example.mangoclient.NetworkUtils;


public class LoginView extends Activity{
	
	private String sessionid = null;
	private LoginViewhandler handler = new LoginViewhandler();
	private ImageView ivCode = null;
	private Button btnLogin = null;
	private Button btnRegister=null;
	private EditText txtUser = null;
	private EditText txtPwd = null;
	private EditText txtCode = null;
	private String result;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ImageThread thread = new ImageThread();
		thread.start();
		Intent intent=getIntent();
		result=intent.getStringExtra("action");
		Log.d("jieguo", result);
		txtUser = (EditText) findViewById(R.id.name);
		txtPwd = (EditText) findViewById(R.id.password);
		txtCode = (EditText) findViewById(R.id.verify);		
		ivCode=(ImageView)findViewById(R.id.verifyimage);
		
		ivCode.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					// TODO Auto-generated method stub
					ImageThread thread = new ImageThread();
					thread.start();	
				}
				// TODO Auto-generated method stub
		});		
		btnLogin=(Button)findViewById(R.id.login);
		btnLogin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
					LoginThread thread = new LoginThread();
					thread.start();	

				
			}
		});
		btnRegister=(Button)findViewById(R.id.register);
		btnRegister.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();  
				intent.setClass(LoginView.this,RegisterActivity.class);   
				startActivity(intent); 		
			}
		});
	}

	class ImageThread extends Thread {

		@Override
		public void run() {
			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_CODE_URL);

			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效,初次发送请求无需设置
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);

			// 创建一个客户端请求发送器
			HttpClient client = new DefaultHttpClient();
			// 发送Get请求，并等待服务端的响应
			try {
				HttpResponse response = client.execute(httpPost);

				// 获得SessionID，并保存，为提交数据做准备
				Header[] header = response.getHeaders("Set-Cookie");
				if (header.length > 0) {
					String temp = header[0].getValue().toString();
					sessionid = temp
							.substring(temp.indexOf("JSESSIONID=") + 11,
									temp.indexOf(";"));
					Log.d("sessionid", sessionid);
				}
				InputStream is = response.getEntity().getContent();
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				is.close();
				showImage(bitmap);

			} catch (ClientProtocolException e) {
				// 服务端如果不支持标准Http协议，则出现这个异常
				showMessage(e.getMessage());
			} catch (IOException e) {
				// 网络通讯发生故障，则出现这个异常
				showMessage("无法连接远程服务器");
			}

		}
	}
	class LoginThread extends Thread {

		@Override
		public void run() {

			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_LOGIN_URL);

			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);
			// 讲验证码，用户名和密码以JSON格式发送给服务端
			try {
				JSONObject params = new JSONObject();
				params.put("code", txtCode.getText().toString());
				params.put("uid", txtUser.getText().toString());
				params.put("pwd", txtPwd.getText().toString());
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

				JSONObject jsons = new JSONObject(responseBody);

				// 验证服务端是否有异常
				if (responseBody.contains(",msg=")){
					showMessage(jsons.getString("msg"));
				} else {
					// 验证服务端返回数据，是否登陆成功
					boolean isok = jsons.getBoolean("isok");
					boolean codeok=jsons.getBoolean("codeok");
					int cusid=jsons.getInt("cusid");
					if (codeok) {
						if(isok){
							if(cusid!=0){
								PublicContainer.customer.setId(cusid);
								showMessage("登陆成功");									
							}
							else{
								showMessage("非注册用户");
							}
						}
						else{
							showMessage("密码或用户名错误");
						}
					} 
					else{
						showMessage("验证码错误");
					}
				}

			} catch (ClientProtocolException e) {
				// 服务端如果不支持标准Http协议，则出现这个异常
				showMessage(e.getMessage());
			} catch (IOException e) {
				// 网络通讯发生故障，则出现这个异常
				showMessage("无法连接远程服务器");
			} catch (JSONException e) {
				// 如果返回的数据格式不满足JSON的格式，则出现这个异常
				showMessage("返回参数错误，登录失败！");
			}

		}
	}

		private void showMessage(String message) {
			Message msg = Message
			.obtain(handler, LoginViewhandler.SHOW_MESSAGE);
			msg.obj = message;
			
			if (message.contains("登陆成功")) {
				msg.arg1 = LoginViewhandler.LOGIN_SUCCESSFUL;
			}
			
			msg.sendToTarget();
			if (!message.contains("无法连接远程服务器")) {
				ImageThread thread = new ImageThread();
				thread.start();
		}
	}

		private void showImage(Bitmap bitmap) {

			// 显示图片的操作不能出现在子线程中，需要由Handler来完成
			// ivImage.setImageBitmap(bitmap);
			Message msg = Message.obtain(handler,
					LoginViewhandler.SHOW_NETWORK_IMAGE);
			msg.obj = bitmap;
			msg.sendToTarget();
		}
		class LoginViewhandler extends Handler {

			// 更新验证码
			public static final int SHOW_NETWORK_IMAGE = 0x0001;
			// 显示错误信息
			public static final int SHOW_MESSAGE = 0x0002;
			// 表示登陆成功
			private static final int LOGIN_SUCCESSFUL = 100; 
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == SHOW_NETWORK_IMAGE) {
					Bitmap bitmap = (Bitmap) msg.obj;
					ivCode.setImageBitmap(bitmap);
				}
				if (msg.what == SHOW_MESSAGE) {
					Toast.makeText(LoginView.this, msg.obj.toString(),
							Toast.LENGTH_LONG).show();
					if(msg.arg1 ==LoginViewhandler.LOGIN_SUCCESSFUL){
						if(result.equalsIgnoreCase("ToppickActivity")){	
							//Intent intent1= new Intent(LoginView.this, MainActivity.class);
							//startActivity(intent1);
							finish();
						}
						if(result.equalsIgnoreCase("MyInfoActivity")){
							Intent intent1= new Intent(LoginView.this, MyInfoActivity.class);
							startActivity(intent1);
							//finish();
						}
						if(result.equalsIgnoreCase("ShopActivity")){
							Intent intent1= new Intent(LoginView.this, ShopActivity.class);
							startActivity(intent1);
							//finish();
						}
						if(result.equalsIgnoreCase("OrderActivity")){
							Intent intent1= new Intent(LoginView.this, OrderActivity.class);
							startActivity(intent1);
							//finish();
						}
						if(result.equalsIgnoreCase("RegisterActivity")){
							Intent intent1= new Intent(LoginView.this, MainActivity.class);
							startActivity(intent1);
						}
					}
				}
			}
		}



}
