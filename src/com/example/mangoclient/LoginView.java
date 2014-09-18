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
			// ����һ��Post�����������������Ҫͨ��Post�������͵�����˵�����ͷ��������Ĳ��������øö�����
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_CODE_URL);

			/* ���������������ʱʱ�� */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// ����sessionid����������session�ռ���Ч,���η���������������
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);

			// ����һ���ͻ�����������
			HttpClient client = new DefaultHttpClient();
			// ����Get���󣬲��ȴ�����˵���Ӧ
			try {
				HttpResponse response = client.execute(httpPost);

				// ���SessionID�������棬Ϊ�ύ������׼��
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
				// ����������֧�ֱ�׼HttpЭ�飬���������쳣
				showMessage(e.getMessage());
			} catch (IOException e) {
				// ����ͨѶ�������ϣ����������쳣
				showMessage("�޷�����Զ�̷�����");
			}

		}
	}
	class LoginThread extends Thread {

		@Override
		public void run() {

			// ����һ��Post�����������������Ҫͨ��Post�������͵�����˵�����ͷ��������Ĳ��������øö�����
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_LOGIN_URL);

			/* ���������������ʱʱ�� */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// ����sessionid����������session�ռ���Ч
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);
			// ����֤�룬�û�����������JSON��ʽ���͸������
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

				JSONObject jsons = new JSONObject(responseBody);

				// ��֤������Ƿ����쳣
				if (responseBody.contains(",msg=")){
					showMessage(jsons.getString("msg"));
				} else {
					// ��֤����˷������ݣ��Ƿ��½�ɹ�
					boolean isok = jsons.getBoolean("isok");
					boolean codeok=jsons.getBoolean("codeok");
					int cusid=jsons.getInt("cusid");
					if (codeok) {
						if(isok){
							if(cusid!=0){
								PublicContainer.customer.setId(cusid);
								showMessage("��½�ɹ�");									
							}
							else{
								showMessage("��ע���û�");
							}
						}
						else{
							showMessage("������û�������");
						}
					} 
					else{
						showMessage("��֤�����");
					}
				}

			} catch (ClientProtocolException e) {
				// ����������֧�ֱ�׼HttpЭ�飬���������쳣
				showMessage(e.getMessage());
			} catch (IOException e) {
				// ����ͨѶ�������ϣ����������쳣
				showMessage("�޷�����Զ�̷�����");
			} catch (JSONException e) {
				// ������ص����ݸ�ʽ������JSON�ĸ�ʽ�����������쳣
				showMessage("���ز������󣬵�¼ʧ�ܣ�");
			}

		}
	}

		private void showMessage(String message) {
			Message msg = Message
			.obtain(handler, LoginViewhandler.SHOW_MESSAGE);
			msg.obj = message;
			
			if (message.contains("��½�ɹ�")) {
				msg.arg1 = LoginViewhandler.LOGIN_SUCCESSFUL;
			}
			
			msg.sendToTarget();
			if (!message.contains("�޷�����Զ�̷�����")) {
				ImageThread thread = new ImageThread();
				thread.start();
		}
	}

		private void showImage(Bitmap bitmap) {

			// ��ʾͼƬ�Ĳ������ܳ��������߳��У���Ҫ��Handler�����
			// ivImage.setImageBitmap(bitmap);
			Message msg = Message.obtain(handler,
					LoginViewhandler.SHOW_NETWORK_IMAGE);
			msg.obj = bitmap;
			msg.sendToTarget();
		}
		class LoginViewhandler extends Handler {

			// ������֤��
			public static final int SHOW_NETWORK_IMAGE = 0x0001;
			// ��ʾ������Ϣ
			public static final int SHOW_MESSAGE = 0x0002;
			// ��ʾ��½�ɹ�
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