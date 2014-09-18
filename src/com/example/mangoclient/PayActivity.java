package com.example.mangoclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.example.helper.PublicContainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PayActivity extends Activity {

	private Double total;
	private DecimalFormat df = new DecimalFormat(".##");
	private String sessionid = null;
	private PayHandler handler = new PayHandler();
	private ArrayList<Address> myAddress = new ArrayList<Address>();
	private ArrayList<String> allAddress = new ArrayList<String>();
	private int addressid = 0;
	private ArrayList<BookList> orderList = new ArrayList<BookList>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay);
		Intent intent = getIntent();

		total = intent.getDoubleExtra("total", 0.0);
		ArrayList<BookList> bookList = intent
				.getParcelableArrayListExtra("booklist");

		for (int i = 0; i < bookList.size(); i++) {
			if (bookList.get(i).isChecked())
				orderList.add(bookList.get(i));
		}

		PayAdapter adapter = new PayAdapter(orderList, this);
		ListView payList = (ListView) findViewById(R.id.pay_list);
		payList.setAdapter(adapter);

		ImageButton btn = (ImageButton) findViewById(R.id.btn_back);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				finish();
			}
		});

		TextView totalText = (TextView) findViewById(R.id.yzh_total);
		totalText.setText("合计:" + df.format(total));

		Button payButton = (Button) findViewById(R.id.yzh_pay);
		payButton.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				PayThread thread = new PayThread();
				thread.start();
			}
		});

		AddressThread thread = new AddressThread();
		thread.start();
	}

	@SuppressLint("ViewHolder")
	class PayAdapter extends BaseAdapter {
		private List<BookList> datas = null;
		private Context context = null;
		private int sum = 0;

		public PayAdapter(List<BookList> datas, Context context) {
			this.datas = datas;
			this.context = context;
		}

		public int getCount() {
			return datas.size();
		}

		public Object getItem(int position) {
			return datas.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			BookList book = datas.get(position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_pay_list, null);

			final ImageView book_image = (ImageView) convertView
					.findViewById(R.id.yzh_cover);
			book_image.setImageBitmap(BitmapFactory.decodeFile(book.getUri()));

			TextView book_name = (TextView) convertView
					.findViewById(R.id.yzh_name);
			book_name.setText(book.getName());

			final TextView book_itemprice = (TextView) convertView
					.findViewById(R.id.yzh_itemprice);
			book_itemprice
					.setText(book.getPrice() + "\nx" + book.getQuantity());

			return convertView;
		}
	}

	class PayThread extends Thread {
		@Override
		public void run() {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			final String date = df.format(new Date());
			HttpPost hpost = new HttpPost(
					NetworkUtils.DANGDANG_ORDERCONFIRM_URL);
			BasicHttpParams bhparams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(bhparams, 5 * 1000);
			if (sessionid == null) {
				hpost.setHeader("cookie", "JESSIONID=" + sessionid);
			}
			hpost.setParams(bhparams);

			JSONObject params = new JSONObject();
			JSONArray array = new JSONArray();
			try {
				params.put("cusid", PublicContainer.customer.getId());
				for (int i = 0; i < orderList.size(); i++) {
					JSONObject jsons = new JSONObject();
					jsons.put("bookid", orderList.get(i).getBookid());
					jsons.put("quantity", orderList.get(i).getQuantity());
					jsons.put("subtotal", orderList.get(i).getQuantity()
							* orderList.get(i).getPrice());
					array.put(jsons);
				}
				params.put("orderitems", array);
				params.put("addressid", addressid);
				params.put("price", total);
				params.put("date", date);
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				hpost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (JSONException e1) {
				showMessage("信息获取错误");
			} catch (UnsupportedEncodingException e) {
				showMessage(e.toString());
			}

			HttpClient hclient = new DefaultHttpClient();

			HttpResponse hresponse;
			try {
				hresponse = hclient.execute(hpost);
				String responseBody = EntityUtils.toString(hresponse
						.getEntity());
				JSONObject jsons = new JSONObject(responseBody);
				if (responseBody.contains(",msg=")) {
					showMessage(jsons.getString("msg"));
				} else {

					boolean isok = jsons.getBoolean("isok");
					if (isok) {
						showMessage("购买成功");
						Intent intent = new Intent();
						intent.setClass(PayActivity.this, OrderActivity.class);
						startActivity(intent);
						finish();
					} else {
						showMessage("购买失败");
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class AddressThread extends Thread {
		@Override
		public void run() {
			HttpPost hpost = new HttpPost(NetworkUtils.DANGDANG_ALL_ADDRESS_URL);
			BasicHttpParams bhparams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(bhparams, 5 * 1000);
			if (sessionid == null) {
				hpost.setHeader("cookie", "JESSIONID=" + sessionid);
			}
			hpost.setParams(bhparams);

			JSONObject params = new JSONObject();
			try {
				params.put("cusid", PublicContainer.customer.getId());
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				hpost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (JSONException e1) {
				showMessage("信息获取错误");
			} catch (UnsupportedEncodingException e) {
				showMessage(e.toString());
			}

			HttpClient hclient = new DefaultHttpClient();

			HttpResponse hresponse;
			try {
				hresponse = hclient.execute(hpost);
				String responseBody = EntityUtils.toString(hresponse
						.getEntity());
				JSONArray array = new JSONArray(responseBody);
				Log.d("add", array.toString());
				for (int i = 0; i < array.length(); i++) {
					JSONObject jsons = array.getJSONObject(i);
					int addressid = jsons.getInt("addressid");
					String receiver = jsons.getString("receiver");
					String country = jsons.getString("country");
					String province = jsons.getString("province");
					String city = jsons.getString("city");
					String district = jsons.getString("district");
					String address = jsons.getString("address");
					String postcode = jsons.getString("postcode");
					String phone = jsons.getString("mobile");

					Address addr = new Address(addressid, receiver, country,
							province, city, district, address, postcode, phone);
					allAddress.add(addr.getDetailAddress());
					myAddress.add(addr);
				}
				ShowSpinner();
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void showMessage(String message) {
		Message msg = Message.obtain(handler, PayHandler.SHOW_MESSAGE);
		msg.obj = message;
		msg.sendToTarget();
	}

	private void ShowSpinner() {
		Message msg = Message.obtain(handler, PayHandler.SHOW_SPINNER);
		msg.sendToTarget();
	}

	class PayHandler extends Handler {

		// 更新验证码
		public static final int SHOW_SPINNER = 0x0001;
		// 显示错误信息
		public static final int SHOW_MESSAGE = 0x0002;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(PayActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
			if (msg.what == SHOW_SPINNER) {
				ArrayAdapter<String> aa = new ArrayAdapter<String>(
						PayActivity.this, android.R.layout.simple_spinner_item,
						allAddress);
				Spinner addrSpinner = (Spinner) findViewById(R.id.yzh_spinner);
				addrSpinner.setAdapter(aa);
				addressid = myAddress.get(0).getAddressId();

				addrSpinner
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							public void onItemSelected(AdapterView<?> arg0,
									View arg1, int position, long id) {
								addressid = myAddress.get(position)
										.getAddressId();
							}

							public void onNothingSelected(AdapterView<?> arg0) {
							}
						});
			}
		}
	}
}
