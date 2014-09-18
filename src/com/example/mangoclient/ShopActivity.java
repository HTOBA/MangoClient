package com.example.mangoclient;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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

import com.example.helper.HttpDownloader;
import com.example.helper.PublicContainer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ShopActivity extends Activity {

	private ArrayList<BookList> bookList = new ArrayList<BookList>();
	private BookList bookItem;
	private ShopHandler handler = new ShopHandler();
	private String sessionid = null;
	private TextView orderquantity;
	private int bookid = 0;
	private Double total = 0.0;
	private String picUri = "sdcard/imagebook/";
	private String downloadUrl = "";
	private String imageUri = "";
	private boolean sdCardExist = Environment.getExternalStorageState().equals(
			android.os.Environment.MEDIA_MOUNTED);
	private ImageView bookImage;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop);

		orderquantity = (TextView) findViewById(R.id.yzh_orderquantity);
		ShopThread thread = new ShopThread();
		thread.start();

		Button payBtn = (Button) findViewById(R.id.yzh_pay);
		payBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("total", total);
				intent.putParcelableArrayListExtra("booklist", bookList);
				intent.setClass(ShopActivity.this, PayActivity.class);
				startActivity(intent);
			}
		});
		
		ImageButton backBtn = (ImageButton) findViewById(R.id.yzh_back);
		backBtn.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				finish();
			}
		});
	}

	private void ShowList() {
		Message msg = Message.obtain(handler, ShopHandler.SHOW_LIST);
		msg.sendToTarget();
	}

	private void showMessage(String message) {
		Message msg = Message.obtain(handler, ShopHandler.SHOW_MESSAGE);
		msg.obj = message;
		msg.sendToTarget();
	}

	@SuppressLint({ "ViewHolder", "InflateParams" })
	class ShopAdapter extends BaseAdapter {

		private List<BookList> datas = null;
		private Context context = null;
		private int sum = 0;
		private DecimalFormat df = new DecimalFormat(".##");

		public ShopAdapter(List<BookList> datas, Context context) {
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
			bookItem = datas.get(position);
			convertView = LayoutInflater.from(context).inflate(
					R.layout.activity_shop_list, null);

			bookImage = (ImageView) convertView
					.findViewById(R.id.yzh_bookcover);

			File file = new File(bookItem.getUri());
			if (!file.exists() || !(file.length() > 0)) {
				downloadUrl = bookItem.getUrl();
				imageUri = bookItem.getUri();
				DownloadThread thread = new DownloadThread();
				thread.start();
			} else
				bookImage.setImageBitmap(BitmapFactory.decodeFile(bookItem
						.getUri()));

			CheckBox checked = (CheckBox) convertView
					.findViewById(R.id.yzh_check);
			TextView book_name = (TextView) convertView
					.findViewById(R.id.yzh_bookname);
			book_name.setText(bookItem.getName());

			final TextView book_quantity = (TextView) convertView
					.findViewById(R.id.yzh_bookquantity);
			book_quantity.setText("" + bookItem.getQuantity());

			TextView book_price = (TextView) convertView
					.findViewById(R.id.yzh_bookprice);
			book_price.setText(df.format(bookItem.getPrice()));

			checked.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton arg0,
						boolean checked) {
					BookList book = bookList.get(position);
					book.setChecked(checked);
					if (checked)
						add(book.getQuantity(), book);
					else
						sub(book.getQuantity(), book);
				}
			});

			ImageButton add = (ImageButton) convertView
					.findViewById(R.id.yzh_add);
			add.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					BookList book = bookList.get(position);
					book.setQuantity(book.getQuantity() + 1);
					book_quantity.setText("" + book.getQuantity());
					if (book.isChecked())
						add(1, book);
				}
			});

			ImageButton mil = (ImageButton) convertView
					.findViewById(R.id.yzh_mil);
			mil.setOnClickListener(new OnClickListener() {

				public void onClick(View arg0) {
					BookList book = bookList.get(position);
					if (book.getQuantity() > 2) {
						book.setQuantity(book.getQuantity() - 1);
						book_quantity.setText("" + book.getQuantity());
						if (book.isChecked()) {
							sub(1, book);
						}
					}
				}
			});

			Button delete = (Button) convertView.findViewById(R.id.yzh_delete);
			delete.setOnClickListener(new OnClickListener() {
				public void onClick(View arg0) {
					sum = 0;
					total = 0.0;
					bookid = bookList.get(position).getBookid();
					orderquantity.setText("共选择0件商品");
					datas.remove(bookItem);
					notifyDataSetChanged();

					DeleteThread thread = new DeleteThread();
					thread.start();
				}
			});
			return convertView;
		}

		public void add(int var, BookList bookItem) {
			sum += var;
			total += var * bookItem.getPrice();
			orderquantity.setText("共选择" + sum + "件商品" + '\t' + "合计"
					+ df.format(total));
		}

		public void sub(int var, BookList bookItem) {
			sum -= var;
			total -= var * bookItem.getPrice();
			orderquantity.setText("共选择" + sum + "件商品" + '\t' + "合计"
					+ df.format(total));
		}
	}

	@SuppressLint("HandlerLeak")
	class ShopHandler extends Handler {

		// 更新验证码
		public static final int SHOW_LIST = 0x0001;
		// 显示错误信息
		public static final int SHOW_MESSAGE = 0x0002;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_LIST) {
				ShopAdapter adapter = new ShopAdapter(bookList,
						ShopActivity.this);
				ListView book_listView = (ListView) findViewById(R.id.yzh_book);
				book_listView.setAdapter(adapter);

				book_listView.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> adapter, View view,
							int position, long id) {
					}
				});
			}
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(ShopActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
		}
	}

	class ShopThread extends Thread {
		public void run() {

			HttpPost hpost = new HttpPost(NetworkUtils.DANGDANG_SHOPPING_URL);
			BasicHttpParams bhparams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(bhparams, 5 * 1000);
			if (sessionid == null) {
				hpost.setHeader("cookie", "JESSIONID=" + sessionid);
			}
			hpost.setParams(bhparams);

			JSONObject params = new JSONObject();
			try {
				params.put("op", "all");
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
				Log.d("int", array.toString());
				for (int i = 0; i < array.length(); i++) {
					final JSONObject jsons = array.getJSONObject(i);
					if (!sdCardExist) {
						showMessage("请插入内存卡");
					} else {
						File dirFirstFile = new File(picUri);
						if (!dirFirstFile.exists()) {// 判断文件夹目录是否存在
							dirFirstFile.mkdir();// 如果不存在则创建
						}
					}

					try {
						String uri = picUri + jsons.getString("imagename");
						String url = NetworkUtils.DANGDANG_BASE_URL
								+ jsons.getString("imagepath");
						String name = jsons.getString("bookname");
						Double price = jsons.getDouble("price");
						int bookid = jsons.getInt("bookid");

						BookList bookitem = new BookList(uri, url, name, price,
								false, bookid);
						bookList.add(bookitem);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				ShowList();
			} catch (JSONException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class DeleteThread extends Thread {
		public void run() {

			HttpPost hpost = new HttpPost(NetworkUtils.DANGDANG_SHOPPING_URL);
			BasicHttpParams bhparams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(bhparams, 5 * 1000);
			if (sessionid == null) {
				hpost.setHeader("cookie", "JESSIONID=" + sessionid);
			}
			hpost.setParams(bhparams);

			JSONObject params = new JSONObject();
			try {
				params.put("op", "delete");
				params.put("cusid", PublicContainer.customer.getId());
				params.put("bookid", bookid);
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
			try {
				HttpResponse hresponse = hclient.execute(hpost);
				String responseBody = EntityUtils.toString(hresponse
						.getEntity());
				JSONObject jsons = new JSONObject(responseBody);
				if (responseBody.contains(",msg=")) {
					showMessage(jsons.getString("msg"));
				} else {
					boolean isok = jsons.getBoolean("isok");
					if (isok)
						showMessage("删除成功");
					else
						showMessage("删除失败");
				}
			} catch (ClientProtocolException e) {
				showMessage(e.toString());
			} catch (IOException e) {
				showMessage(e.toString());
			} catch (JSONException e) {
				showMessage(e.toString());
			}
		}
	}

	class DownloadThread extends Thread {
		public void run() {
			final String uri = imageUri;
			final ImageView image = bookImage;
			HttpDownloader downloader = new HttpDownloader();
			downloader.download(downloadUrl, picUri.substring(7));
			handler.post(new Runnable() {
				public void run() {
					image.setImageBitmap(BitmapFactory.decodeFile(uri));
				}
			});
		}
	}
}
