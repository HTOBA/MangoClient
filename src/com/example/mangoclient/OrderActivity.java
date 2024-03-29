package com.example.mangoclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.helper.PublicContainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class OrderActivity extends Activity {
	final int cusid = PublicContainer.customer.getId();
	ListView tempListView = null;
	JSONArray orderListJson = null;
	Button undo = null;
	Button done = null;
	List<Map<String, Object>> orderCompleted = new ArrayList<Map<String, Object>>();
	List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();

	protected void onCreate(Bundle savedInstanceState) {
		OrderThread thread = new OrderThread();
		thread.start();
		while (orderListJson == null)
			;

		Log.d("content", orderListJson.toString());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_orders);
		undo = (Button) findViewById(R.id.yzh_undo);
		done = (Button) findViewById(R.id.yzh_done);
		LinearLayout orders = (LinearLayout) (findViewById(R.id.yzh_orderslayout));
		int orderNum = orderListJson.length();
		for (int i = 0; i < orderNum; i++) {
			try {

				JSONObject order = orderListJson.getJSONObject(i);
				JSONArray orderItems = order.getJSONArray("orderitems");
				for (int j = 0; j < orderItems.length(); j++) {
					Map<String, Object> tempMap = new HashMap<String, Object>();
					JSONObject orderitem = orderItems.getJSONObject(j);
					String bookname = orderitem.getString("bookname");
					Log.d("bookname", bookname);
					String image = NetworkUtils.DANGDANG_BASE_URL
							+ orderitem.getString("imgpath");
					int quantity = orderitem.getInt("quantity");
					double price = orderitem.getDouble("subtotal");
					int bookid = orderitem.getInt("bookid");
					tempMap.put("image", image);
					tempMap.put("name", bookname);
					tempMap.put("price", price);
					tempMap.put("bookid", bookid);
					datas.add(tempMap);
				}

				// temp.addView(tempListView);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// orders.addView(temp);

		}
		tempListView = new ListView(this);
		MyOrderAdapter moa = new MyOrderAdapter(datas, this);
		tempListView.setAdapter(moa);
		tempListView.setFocusable(false);
		orders.addView(tempListView);
		undo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				MyOrderAdapter ba = new MyOrderAdapter(datas,
						OrderActivity.this);// TODO Auto-generated method stub
				tempListView.setAdapter(ba);
			}
		});
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				CompletedOrderAdapter coa = new CompletedOrderAdapter(
						orderCompleted, OrderActivity.this);// TODO
															// Auto-generated
															// method stub
				tempListView.setAdapter(coa);
			}
		});
	}

	class OrderThread extends Thread {

		@Override
		public void run() {
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_ORDER_URL);
			// HttpPost httpPost = new
			// HttpPost("http://localhost/MangGoServer/orderslist.json");
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
				params.put("cusid", cusid);
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e1) {
				Log.d("order", "编码错误");
			} catch (JSONException e1) {
				Log.d("order", "json错误");
			}

			// 创建一个客户端请求发送器
			HttpClient client = new DefaultHttpClient();

			// 发送Get请求，并等待服务端的响应
			try {
				HttpResponse response = client.execute(httpPost);
				String responseBody = EntityUtils
						.toString(response.getEntity());
				orderListJson = new JSONArray(responseBody);
				// Message msg = new Message();
				// msg.obj = orderListJson;
				// msg.what = 88;
				// msg.sendToTarget();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public class MyOrderAdapter extends BaseAdapter {
		private Handler handler = new Handler();
		private List<Map<String, Object>> datas = null;
		private Context context = null;
		private Bitmap bitmap;

		public MyOrderAdapter(List<Map<String, Object>> datas, Context context) {
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

		@SuppressLint("ViewHolder")
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_my_orderitem, null);
			final ImageView book_image = (ImageView) convertView
					.findViewById(R.id.yzh_cover);
			final String image = (String) datas.get(position).get("image");
			Log.d("image", image);
			TextView book_name = (TextView) convertView
					.findViewById(R.id.yzh_name);
			final String bookname = datas.get(position).get("name").toString();
			book_name.setText(bookname);
			final String price = datas.get(position).get("price").toString();
			TextView book_price = (TextView) convertView
					.findViewById(R.id.yzh_itemprice);
			book_price.setText(price);

			new Thread(new Runnable() {
				public void run() {
					final ImageView temp = book_image;
					try {
						final Bitmap cover;
						cover = BitmapFactory.decodeStream(new URL(image)
								.openStream());
						bitmap = cover;
						handler.post(new Runnable() {
							public void run() {
								temp.setImageBitmap(cover);
							}
						});
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			final int bookid = (Integer) datas.get(position).get("bookid");
			Button comment = (Button) convertView.findViewById(R.id.comment);
			comment.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Builder builder = new AlertDialog.Builder(context);// TODO
																		// Auto-generated
																		// method
																		// stub
					builder.setIcon(R.drawable.ic_launcher);

					LayoutInflater layoutInflater = LayoutInflater
							.from(OrderActivity.this);
					View longinDialogView = layoutInflater.inflate(
							R.layout.commentlayout, null);
					final EditText comment = (EditText) longinDialogView
							.findViewById(R.id.editText1);
					builder.setView(longinDialogView);
					builder.setTitle("请亲写下评论：");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									SimpleDateFormat df = new SimpleDateFormat(
											"yyyy-MM-dd HH:mm:ss");// 设置日期格式
									final String date = df.format(new Date());
									final String commentContent = comment
											.getText().toString().trim();
									Log.d("content", commentContent);
									Map<String, Object> tempMap = new HashMap<String, Object>();
									tempMap.put("cover", bitmap);
									tempMap.put("bookname", bookname);
									tempMap.put("price", price);
									tempMap.put("bookid", bookid);
									orderCompleted.add(tempMap);

									datas.remove(position);
									Thread thread = new Thread() {
										@Override
										public void run() {
											HttpPost httpPost = new HttpPost(
													NetworkUtils.DANGDANG_COMMENTCOMMIT_URL);
											BasicHttpParams httpParameters = new BasicHttpParams();
											HttpConnectionParams
													.setConnectionTimeout(
															httpParameters,
															5 * 1000);
											httpPost.setParams(httpParameters);
											try {
												JSONObject params = new JSONObject();
												params.put("cusid", cusid);
												params.put("bookid", bookid);
												params.put("content",
														commentContent);
												params.put("date", date);
												params.put("star", 4);
												List<NameValuePair> nvps = new ArrayList<NameValuePair>();
												BasicNameValuePair se = new BasicNameValuePair(
														"params", params
																.toString());
												nvps.add(se);
												httpPost.setEntity(new UrlEncodedFormEntity(
														nvps, HTTP.UTF_8));
											} catch (UnsupportedEncodingException e1) {
												Log.d("order", "编码错误");
											} catch (JSONException e1) {
												Log.d("order", "json错误");
											}
											HttpClient client = new DefaultHttpClient();
											try {
												HttpResponse response = client
														.execute(httpPost);
											} catch (ClientProtocolException e) {
												e.printStackTrace();
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									};
									thread.start();
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									// TODO Auto-generated method stub

								}
							});
					builder.create().show();
				}
			});
			return convertView;
		}

	}

	public class CompletedOrderAdapter extends BaseAdapter {
		private List<Map<String, Object>> datas = null;
		private Context context = null;

		public CompletedOrderAdapter(List<Map<String, Object>> datas,
				Context context) {
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

		@SuppressLint("ViewHolder")
		public View getView(final int position, View convertView,
				ViewGroup parent) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.ordercompleted, null);
			final ImageView book_image = (ImageView) convertView
					.findViewById(R.id.completed_cover);
			final Bitmap image = (Bitmap) datas.get(position).get("cover");
			TextView book_name = (TextView) convertView
					.findViewById(R.id.completed_name);
			book_name.setText(datas.get(position).get("bookname").toString());
			TextView book_price = (TextView) convertView
					.findViewById(R.id.completed_itemprice);
			book_price.setText(datas.get(position).get("price").toString());
			final int bookid = (Integer) datas.get(position).get("bookid");
			if (book_image.getDrawable() == null) {
				book_image.setImageBitmap(image);
			}
			return convertView;
		}

	}
}
