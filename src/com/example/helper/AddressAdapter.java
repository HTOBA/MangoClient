package com.example.helper;

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

import com.example.mangoclient.*;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AddressAdapter extends ArrayAdapter<Address> {
	private int resourceId;
	private Address address;
	private boolean pass_ok = false;
	private boolean delete_success = false;
	private Handler handler;

	public AddressAdapter(Handler handler, Context context,
			int textViewResourceId, List<Address> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		this.handler = handler;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		address = getItem(position); // 获取当前项的address实例
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView txt_address = (TextView) view.findViewById(R.id.txt_address);
		TextView txt_postcode = (TextView) view.findViewById(R.id.txt_postcode);
		TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
		TextView txt_phone_number = (TextView) view
				.findViewById(R.id.txt_phone_number);
		txt_address.setText("收货地址：" + address.getWholeAddress());
		txt_postcode.setText("邮编：" + address.getPostcode());
		txt_name.setText("收货人：" + address.getName());
		txt_phone_number.setText("联系电话：" + address.getPhoneNumber());

		Button btn_delete = (Button) view.findViewById(R.id.btn_delete_address);
		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				address = AddressManageMainActivity.addressList.get(position);
				// 弹出对话框进行确认。
				AlertDialog.Builder dialog = AddressManageMainActivity.dialog_ensure;
				dialog.setTitle("确认删除？");
				dialog.setMessage("此操作将删除所选地址信息（收货地址为："
						+ address.getWholeAddress() + "），点击“确定”继续……");
				dialog.setCancelable(false);
				dialog.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Log.d("hahaha",
										AddressManageMainActivity.addressList
												.toString());
								DeleteAddressThread dthread = new DeleteAddressThread();
								dthread.start();
								while (!pass_ok) {
								} // 等待线程结束
								pass_ok = false;
								if (delete_success) {
									remove(address);
									notifyDataSetChanged();
								}
							}
						});
				dialog.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// Do nothing
							}
						});
				dialog.show();
			}
		});

		return view;
	}

	// public void initDeleteDialog() {
	//
	// }

	class DeleteAddressThread extends Thread {

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
				params.put("op", "delete");
				params.put("addressid", address.getAddressId());
				Log.d("AddressAdapter", "addressid = " + address.getAddressId());
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
				Log.d("AddressAdapter",
						"responsebody: " + responseBody.toString());

				JSONObject jsons = new JSONObject(responseBody);
				// 验证服务端是否有异常
				if (responseBody.contains(",msg=")) {
					showMessage(jsons.getString("msg"));
				} else {
					delete_success = jsons.getBoolean("isok");
					if (delete_success) {
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

}
