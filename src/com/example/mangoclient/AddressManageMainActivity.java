// 如果用户没有收货地址，怎么办？
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.helper.Address;
import com.example.helper.AddressAdapter;
import com.example.helper.PublicContainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class AddressManageMainActivity extends Activity implements
		OnClickListener {

	// 显示错误信息
	public static final int SHOW_MESSAGE = 1;
	public static List<Address> addressList = new ArrayList<Address>();
	public static AlertDialog.Builder dialog_ensure;

	private Button btn_back; // 返回按钮
	private Button btn_add_address;

	AddressAdapter adapter;

	private boolean pass_ok = false; // 用于while循环等待的信号
	private AddressMainHandler handler = new AddressMainHandler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("AddressMain", "onCreate");
		setContentView(R.layout.address_manage_main);
		if (addressList.isEmpty()) {
			initAddresses();
		} else if (PublicContainer.need_update_address) {
			updateAddress();
		}
		dialog_ensure = new AlertDialog.Builder(AddressManageMainActivity.this);
		adapter = new AddressAdapter(handler, AddressManageMainActivity.this,
				R.layout.address_item, addressList);
		ListView listView = (ListView) findViewById(R.id.address_list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d("my_tag", "position is : " + position);
				startDetailActivity(position);
			}
		});

		btn_back = (Button) findViewById(R.id.btn_back_in_address_main);
		btn_add_address = (Button) findViewById(R.id.btn_add_address);
		btn_back.setOnClickListener(this);
		btn_add_address.setOnClickListener(this);

		Toast.makeText(this, "点击列表项查看详情", Toast.LENGTH_SHORT).show();
	}

	private void initAddresses() {
		if (PublicContainer.has_init_addresses) {
			addressList = PublicContainer.customer.getAddresses();
		} else {
			updateAddress();
		}
	}

	private void updateAddress() {
		UpdateAddressThread uathread = new UpdateAddressThread();
		uathread.start();
		while (!pass_ok) {
		} // 等待地址初始化完成
		pass_ok = false;
		PublicContainer.need_update_address = false;

	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d("hehe", "onRestart");
		if (PublicContainer.need_update_address) {
			addressList.clear();
			Log.d("hehe", "1 addressList : " + addressList.toString());
			Log.d("hehe", "1 PublicContainer.address : "
					+ PublicContainer.customer.getAddresses().toString());
			updateAddress();
			adapter.notifyDataSetChanged();
			Log.d("hehe", "2 addressList : " + addressList.toString());
			Log.d("hehe", "2 PublicContainer.address : "
					+ PublicContainer.customer.getAddresses().toString());
		}
	}

	// 启动“地址详情页面”
	public void startDetailActivity(int address_index) {
		Intent intent = new Intent(AddressManageMainActivity.this,
				AddressManageDetailActivity.class);
		intent.putExtra("address_index", address_index);
		Log.d("my_tag", "just before startActivity");
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back_in_address_main:
			finish();
			break;
		case R.id.btn_add_address:
			Intent intent = new Intent(AddressManageMainActivity.this,
					AddressManageAddActivity.class);
			startActivity(intent);
			break;
		default:
		}
	}

	class UpdateAddressThread extends Thread {

		@Override
		public void run() {
			Log.v("haha", "address main runs");

			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(
					NetworkUtils.DANGDANG_ALL_ADDRESS_URL);

			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			httpPost.setParams(httpParameters);

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
				Log.d("address", "responsebody: " + responseBody.toString());

				parseJSONWithJSONObject(responseBody);

			} catch (ClientProtocolException e) {
				// 服务端如果不支持标准Http协议，则出现这个异常
				showMessage(e.getMessage());
			} catch (IOException e) {
				// 网络通讯发生故障，则出现这个异常
				showMessage("无法连接远程服务器");
			} finally {
				pass_ok = true;
			}

		}
	}

	private void showMessage(String message) {
		Message msg = Message.obtain(handler, SHOW_MESSAGE);
		msg.obj = message;
		msg.sendToTarget();
	}

	private void parseJSONWithJSONObject(String jsonData) {
		try {
			JSONArray jsonArray = new JSONArray(jsonData);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				int addressid = jsonObject.getInt("addressid");
				String receiver = jsonObject.getString("receiver");
				String country = jsonObject.getString("country");
				String province = jsonObject.getString("province");
				String city = jsonObject.getString("city");
				String district = jsonObject.getString("district");
				String detail_address = jsonObject.getString("address");
				String postcode = jsonObject.getString("postcode");
				String phone_number = jsonObject.getString("mobile");
				Address address = new Address(addressid, receiver, country,
						province, city, district, detail_address, postcode,
						phone_number);
				addressList.add(address);
			}
			PublicContainer.customer.setAddresses(addressList);
			PublicContainer.has_init_addresses = true;
			pass_ok = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("HandlerLeak")
	class AddressMainHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(AddressManageMainActivity.this,
						msg.obj.toString(), Toast.LENGTH_LONG).show();
			}
		}
	}
}
