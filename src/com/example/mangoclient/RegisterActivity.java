package com.example.mangoclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {
	
	private String sessionid=null;
	private EditText regname=null;
	private EditText realname=null;
	private EditText mobile=null;
	private EditText password=null;
	private EditText email=null;
	private Button btnsure=null;
	private registeractivity handler = new registeractivity();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		regname=(EditText) findViewById(R.id.regname);
		realname=(EditText) findViewById(R.id.regrelname);
		mobile=(EditText) findViewById(R.id.regmobile);
		password=(EditText) findViewById(R.id.regpassword);
		email=(EditText) findViewById(R.id.regemail);
		btnsure=(Button)findViewById(R.id.sure);
		btnsure.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				RegisterThread rt=new RegisterThread();
				rt.start();	
				Intent intent=new Intent(RegisterActivity.this,LoginView.class);
				intent.putExtra("action", "RegisterActivity");
				startActivity(intent);
			}
		});
		Button btncancle=(Button) findViewById(R.id.cancle);
		btncancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
				startActivity(intent);
			}
		});
		
	}
	class RegisterThread extends Thread {

		@Override
		public void run() {

			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_REGISTER_URL);

			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
			}
			httpPost.setParams(httpParameters);
			// 讲验证码，用户名和密码以JSON格式发送给服务端
			try {
				JSONObject params = new JSONObject();
				params.put("regname",regname.getText().toString());
				params.put("realname",realname.getText().toString());
				params.put("mobile",mobile.getText().toString());
				params.put("password",password.getText().toString());
				params.put("email",email.getText().toString());
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
					boolean isrepeated = jsons.getBoolean("isrepeated");
					int cusid=jsons.getInt("cusid");
					Log.d("response",responseBody);
					if(!isrepeated){
						if(cusid!=0){
							showMessage("注册成功");
						}
						
					}else{
						showMessage("该用户名已经注册过");				
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
		private void showMessage(String message) {
			Message msg = Message
			.obtain(handler, registeractivity.SHOW_MESSAGE);
			msg.obj = message;
			msg.sendToTarget();
		}
	}
	class registeractivity extends Handler {
		// 显示错误信息
		public static final int SHOW_MESSAGE = 0x0002;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(RegisterActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
