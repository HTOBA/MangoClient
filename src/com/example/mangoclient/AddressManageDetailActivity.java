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
import com.example.helper.Images;
import com.example.helper.PublicContainer;
import com.example.mangoclient.MyInfoActivity.MyInfoHandler;

import android.annotation.SuppressLint;
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

public class AddressManageDetailActivity extends Activity implements
		OnClickListener {

	private Address currentAddress;

	private EditText edit_name;
	private EditText edit_country;
	private EditText edit_province;
	private EditText edit_city;
	private EditText edit_district;
	private EditText edit_detail_address;
	private EditText edit_postcode;
	private EditText edit_phone_number;

	private String name;
	private String country;
	private String province;
	private String city;
	private String district;
	private String detail_address;
	private String postcode;
	private String phone_number;

	private Button btn_edit;
	private Button btn_save;
	private Button btn_cancel;
	private Button btn_back_in_address_detail;

	private AddressDetailHandler handler = new AddressDetailHandler();

	private boolean pass_ok = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_manage_detail);
		Intent intent = getIntent();
		int address_index = intent.getIntExtra("address_index", -1);
		if (address_index != (-1)) {
			currentAddress = AddressManageMainActivity.addressList
					.get(address_index);
			initData(currentAddress);
		}

		// 按钮监听
		initButton(); // 初始化按钮
	}

	// 根据提供的address实例，初始化文本框内的值
	private void initData(Address address) {
		edit_name = (EditText) findViewById(R.id.edit_name);
		edit_country = (EditText) findViewById(R.id.edit_country);
		edit_province = (EditText) findViewById(R.id.edit_province);
		edit_city = (EditText) findViewById(R.id.edit_city);
		edit_district = (EditText) findViewById(R.id.edit_district);
		edit_detail_address = (EditText) findViewById(R.id.edit_detail_address);
		edit_postcode = (EditText) findViewById(R.id.edit_postcode);
		edit_phone_number = (EditText) findViewById(R.id.edit_phone_number);

		edit_name.setText(address.getName());
		edit_country.setText(address.getCountry());
		edit_province.setText(address.getProvince());
		edit_city.setText(address.getCity());
		edit_district.setText(address.getDistrict());
		edit_detail_address.setText(address.getDetailAddress());
		edit_postcode.setText(address.getPostcode());
		edit_phone_number.setText(address.getPhoneNumber());

		setEditTextEnable(false);
	}

	private void setEditTextEnable(boolean bool) {
		edit_name.setEnabled(bool);
		edit_country.setEnabled(bool);
		edit_province.setEnabled(bool);
		edit_city.setEnabled(bool);
		edit_district.setEnabled(bool);
		edit_detail_address.setEnabled(bool);
		edit_postcode.setEnabled(bool);
		edit_phone_number.setEnabled(bool);
	}

	private void initButton() {
		btn_edit = (Button) findViewById(R.id.btn_edit);
		btn_save = (Button) findViewById(R.id.btn_save);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_back_in_address_detail = (Button) findViewById(R.id.btn_back_in_address_detail);
		btn_edit.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_back_in_address_detail.setOnClickListener(this);

		btn_save.setVisibility(View.GONE);
		btn_cancel.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_edit:
			setEditTextEnable(true); // 让文本框可编辑
			btn_edit.setVisibility(View.GONE);
			btn_save.setVisibility(View.VISIBLE);
			btn_cancel.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_save:
			btn_save.setVisibility(View.GONE);
			btn_cancel.setVisibility(View.GONE);
			btn_edit.setVisibility(View.VISIBLE);
			setEditTextEnable(false);

			name = edit_name.getText().toString();
			country = edit_country.getText().toString();
			province = edit_province.getText().toString();
			city = edit_city.getText().toString();
			district = edit_district.getText().toString();
			detail_address = edit_detail_address.getText().toString();
			postcode = edit_postcode.getText().toString();
			phone_number = edit_phone_number.getText().toString();
			// TODO 启动线程修改服务器数据
			CommitAddressThread cathread = new CommitAddressThread();
			cathread.start();
			while (!pass_ok) {
			}
			pass_ok = false; // 等待线程结束
			currentAddress.setName(edit_name.getText().toString());
			currentAddress.setCountry(edit_country.getText().toString());
			currentAddress.setProvince(edit_province.getText().toString());
			currentAddress.setCity(edit_city.getText().toString());
			currentAddress.setDistrict(edit_district.getText().toString());
			currentAddress.setDetailAddress(edit_detail_address.getText()
					.toString());
			currentAddress.setPostcode(edit_postcode.getText().toString());
			currentAddress.setPhoneNumber(edit_phone_number.getText()
					.toString());
			break;
		case R.id.btn_cancel:
			btn_save.setVisibility(View.GONE);
			btn_cancel.setVisibility(View.GONE);
			btn_edit.setVisibility(View.VISIBLE);
			setEditTextEnable(false);
			edit_name.setText(currentAddress.getName());
			edit_country.setText(currentAddress.getCountry());
			edit_province.setText(currentAddress.getProvince());
			edit_city.setText(currentAddress.getCity());
			edit_district.setText(currentAddress.getDistrict());
			edit_detail_address.setText(currentAddress.getDetailAddress());
			edit_postcode.setText(currentAddress.getPostcode());
			edit_phone_number.setText(currentAddress.getPhoneNumber());
			break;
		case R.id.btn_back_in_address_detail:
			finish();
		default:
		}
	}

	// 修改服务器的地址数据
	class CommitAddressThread extends Thread {

		@Override
		public void run() {
			Log.v("AddressDetail", "modify address thread runs");

			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(
					NetworkUtils.DANGDANG_ADDRESS_CHANGE_URL);

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
				// params.put("cusid", ""+PublicContainer.customer.getId());
				params.put("op", "modify");
				params.put("addressid", currentAddress.getAddressId());
				params.put("consignmentName", name);
				params.put("country", "001");
				params.put("province", "108");
				params.put("city", "129");
				params.put("district", "198");
				params.put("streetaddress", detail_address);
				params.put("postcode", postcode);
				params.put("mobile", phone_number);
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
				Log.d("AddressDetail",
						"responsebody: " + responseBody.toString());

				JSONObject jsons = new JSONObject(responseBody);
				Log.d("AddressDetail", "just after jsonnnn");
				// 验证服务端是否有异常
				if (responseBody.contains(",msg=")) {
					Log.d("MyInfoActivity", "contains ',msg='");
					showMessage(jsons.getString("msg"));
				} else {
					if (jsons.getBoolean("isok")) {
						showMessage("修改成功");
						PublicContainer.need_update_address = true;
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
		Message msg = Message
				.obtain(handler, AddressDetailHandler.SHOW_MESSAGE);
		msg.obj = message;
		Log.d("AddressDetail", "msg.obj = " + message);
		msg.sendToTarget();
	}

	@SuppressLint("HandlerLeak")
	class AddressDetailHandler extends Handler {
		// 显示错误信息
		public static final int SHOW_MESSAGE = 1;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(AddressManageDetailActivity.this,
						msg.obj.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}

}
