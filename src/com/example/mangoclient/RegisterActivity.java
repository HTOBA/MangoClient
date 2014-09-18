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

			// ����һ��Post�����������������Ҫͨ��Post�������͵�����˵�����ͷ��������Ĳ��������øö�����
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_REGISTER_URL);

			/* ���������������ʱʱ�� */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// ����sessionid����������session�ռ���Ч
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
			}
			httpPost.setParams(httpParameters);
			// ����֤�룬�û�����������JSON��ʽ���͸������
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
					boolean isrepeated = jsons.getBoolean("isrepeated");
					int cusid=jsons.getInt("cusid");
					Log.d("response",responseBody);
					if(!isrepeated){
						if(cusid!=0){
							showMessage("ע��ɹ�");
						}
						
					}else{
						showMessage("���û����Ѿ�ע���");				
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
		private void showMessage(String message) {
			Message msg = Message
			.obtain(handler, registeractivity.SHOW_MESSAGE);
			msg.obj = message;
			msg.sendToTarget();
		}
	}
	class registeractivity extends Handler {
		// ��ʾ������Ϣ
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
