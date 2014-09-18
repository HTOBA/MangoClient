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
		address = getItem(position); // ��ȡ��ǰ���addressʵ��
		View view = LayoutInflater.from(getContext()).inflate(resourceId, null);
		TextView txt_address = (TextView) view.findViewById(R.id.txt_address);
		TextView txt_postcode = (TextView) view.findViewById(R.id.txt_postcode);
		TextView txt_name = (TextView) view.findViewById(R.id.txt_name);
		TextView txt_phone_number = (TextView) view
				.findViewById(R.id.txt_phone_number);
		txt_address.setText("�ջ���ַ��" + address.getWholeAddress());
		txt_postcode.setText("�ʱࣺ" + address.getPostcode());
		txt_name.setText("�ջ��ˣ�" + address.getName());
		txt_phone_number.setText("��ϵ�绰��" + address.getPhoneNumber());

		Button btn_delete = (Button) view.findViewById(R.id.btn_delete_address);
		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				address = AddressManageMainActivity.addressList.get(position);
				// �����Ի������ȷ�ϡ�
				AlertDialog.Builder dialog = AddressManageMainActivity.dialog_ensure;
				dialog.setTitle("ȷ��ɾ����");
				dialog.setMessage("�˲�����ɾ����ѡ��ַ��Ϣ���ջ���ַΪ��"
						+ address.getWholeAddress() + "���������ȷ������������");
				dialog.setCancelable(false);
				dialog.setPositiveButton("ȷ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Log.d("hahaha",
										AddressManageMainActivity.addressList
												.toString());
								DeleteAddressThread dthread = new DeleteAddressThread();
								dthread.start();
								while (!pass_ok) {
								} // �ȴ��߳̽���
								pass_ok = false;
								if (delete_success) {
									remove(address);
									notifyDataSetChanged();
								}
							}
						});
				dialog.setNegativeButton("ȡ��",
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

			// ����һ��Post�����������������Ҫͨ��Post�������͵�����˵�����ͷ��������Ĳ��������øö�����
			HttpPost httpPost = new HttpPost(
					NetworkUtils.DANGDANG_ADDRESS_CHANGE_URL);

			/* ���������������ʱʱ�� */
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
				Log.d("AddressAdapter",
						"responsebody: " + responseBody.toString());

				JSONObject jsons = new JSONObject(responseBody);
				// ��֤������Ƿ����쳣
				if (responseBody.contains(",msg=")) {
					showMessage(jsons.getString("msg"));
				} else {
					delete_success = jsons.getBoolean("isok");
					if (delete_success) {
						showMessage("�����ɹ���");
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
				showMessage("���ز������󣬻�ȡ�û���Ϣʧ�ܣ�");
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
