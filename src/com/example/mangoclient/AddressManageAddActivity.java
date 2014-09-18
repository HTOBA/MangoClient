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

import com.example.helper.Address;
import com.example.helper.PublicContainer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddressManageAddActivity extends Activity {

	private EditText[] edit_text = new EditText[8]; // 8个引用
	private Address new_address;

	private AddressAddHandler handler = new AddressAddHandler();
	private boolean pass_ok = false;
	private boolean add_success = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_manage_add);

		initEditText();

		Button btn_ensure = (Button) findViewById(R.id.btn_address_add_ensure);
		btn_ensure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (false == checkReady()) {
					Toast.makeText(AddressManageAddActivity.this, "请输入所有信息",
							Toast.LENGTH_SHORT).show();
				} else {
					String txt_name = edit_text[0].getText().toString().trim();
					String txt_country = edit_text[1].getText().toString()
							.trim();
					String txt_province = edit_text[2].getText().toString()
							.trim();
					String txt_city = edit_text[3].getText().toString().trim();
					String txt_district = edit_text[4].getText().toString()
							.trim();
					String txt_detail_address = edit_text[5].getText()
							.toString().trim();
					String txt_postcode = edit_text[6].getText().toString()
							.trim();
					String txt_phone_number = edit_text[7].getText().toString()
							.trim();

					new_address = new Address(0, txt_name, txt_country,
							txt_province, txt_city, txt_district,
							txt_detail_address, txt_postcode, txt_phone_number);
					// TODO 将数据库操作请求发送到服务端
					AddAddressThread aathread = new AddAddressThread();
					aathread.start();
					while (!pass_ok) {
					} // 等待线程结束
					pass_ok = false;
					if (add_success) {
						PublicContainer.need_update_address = true;
					}
					finish();
				}
			}
		});

		Button btn_back = (Button) findViewById(R.id.btn_back_in_address_add);
		btn_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void initEditText() {
		edit_text[0] = (EditText) findViewById(R.id.edit_name_new);
		edit_text[1] = (EditText) findViewById(R.id.edit_country_new);
		edit_text[2] = (EditText) findViewById(R.id.edit_province_new);
		edit_text[3] = (EditText) findViewById(R.id.edit_city_new);
		edit_text[4] = (EditText) findViewById(R.id.edit_district_new);
		edit_text[5] = (EditText) findViewById(R.id.edit_detail_address_new);
		edit_text[6] = (EditText) findViewById(R.id.edit_postcode_new);
		edit_text[7] = (EditText) findViewById(R.id.edit_phone_number_new);
	}

	boolean checkReady() {
		for (EditText et : edit_text) {
			if ("".equals(et.getText().toString().trim())) {
				return false;
			}
		}
		return true;
	}

	class AddAddressThread extends Thread {

		@Override
		public void run() {
			Log.v("AddressAdapter", "address delete runs");

			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(
					NetworkUtils.DANGDANG_ADDRESS_CHANGE_URL);
			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			httpPost.setParams(httpParameters);

			try {
				JSONObject params = new JSONObject();
				// params.put("cusid", ""+PublicContainer.customer.getId());
				params.put("op", "add");
				params.put("cusid", PublicContainer.customer.getId());
				// params.put("cusid",1);
				params.put("consignmentName", new_address.getName());
				// params.put("country", new_address.getCountry());
				// params.put("province", new_address.getProvince());
				// params.put("city", new_address.getCity());
				// params.put("district", new_address.getDistrict());
				params.put("country", "001");
				params.put("province", "107");
				params.put("city", "137");
				params.put("district", "194");
				params.put("streetaddress", new_address.getDetailAddress());
				params.put("postcode", new_address.getPostcode());
				params.put("mobile", new_address.getPhoneNumber());

				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
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
				Log.d("AddressAdapter",
						"responsebody: " + responseBody.toString());

				JSONObject jsons = new JSONObject(responseBody);
				// 验证服务端是否有异常
				if (responseBody.contains(",msg=")) {
					showMessage(jsons.getString("msg"));
				} else {
					add_success = jsons.getBoolean("isok");
					if (add_success) {
						showMessage("操作成功！");
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
				showMessage("返回参数错误，获取用户信息失败！");
			} finally {
				pass_ok = true;
			}

		}
	}

	private void showMessage(String message) {
		Message msg = Message.obtain(handler,
				AddressManageMainActivity.SHOW_MESSAGE);
		msg.obj = message;
		msg.sendToTarget();
	}

	class AddressAddHandler extends Handler {
		// 显示错误信息
		public static final int SHOW_MESSAGE = 1;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(AddressManageAddActivity.this,
						msg.obj.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
}
