package com.example.mangoclient;

/*
 * 1、注意android-support-v4.jar的引入和使用方法，具体操作参考网址:
 * http://developer.android.com/sdk/compatibility-library.html
 * 
 * 2、注意main.xml文件中include方法引入其它布局文件，以及ViewPager控件的布局方式。
 * 
 * 3、该Demo的各个布局文件的设计非常好，应该多学习一下，以后可以参考
 * 
 * 4、发现在setContentView时，通过一下两种方式实现，得到的效果是不一样的：
 *    1)setContentView(R.layout.main);
 *    2)mainViewGroup = (ViewGroup) mInflater.inflate(R.layout.main, null);
 *    ……………………………………
 *    setContentView(mainViewGroup);
 *    
 *    其中，使用前者效果不正常，使用后者效果正常。
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.adapter.CommentAdapter;
import com.example.helper.Book;
import com.example.helper.PublicContainer;

//import com.capricorn.ArcMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//new

public class ToppickActivity extends Activity {

	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	private ImageView mImageView;
	private ImageView[] mImageViews;

	// 该应用的主布局LinearLayout
	private ViewGroup mainViewGroup;
	// 主布局底部指示当前页面的小圆点视图，LinearLayout
	private ViewGroup indicatorViewGroup;
	private Book book;
	// 定义LayoutInflater
	LayoutInflater mInflater;
	private int id;
	private Uri imageUri;
	private String sessionid = null;
	private String[] content;
	private String[] contentdate;
	private int counter;
	private settexthandler handler = null;
	private Context context = null;
	private List<Map<String, Object>> bookList;

	// new

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		handler = new settexthandler();
		Intent intent = getIntent();
		id = intent.getIntExtra("id", 0);
		File image = new File(intent.getStringExtra("picdirpath"),
				intent.getStringExtra("imagename"));
		imageUri = Uri.fromFile(image);
		bookList = new ArrayList<Map<String, Object>>();
		Log.d("lujingtupian", imageUri.toString());
		Receive receive = new Receive();
		receive.start();
		Receivecomments receivecomments = new Receivecomments();
		receivecomments.start();
		// 设置窗口无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mInflater = getLayoutInflater();

		mPageViews = new ArrayList<View>();
		mPageViews.add(mInflater.inflate(R.layout.item01, null));
		mPageViews.add(mInflater.inflate(R.layout.item02, null));
		mPageViews.add(mInflater.inflate(R.layout.item03, null));
		mImageViews = new ImageView[mPageViews.size()];

		mainViewGroup = (ViewGroup) mInflater.inflate(R.layout.main, null);

		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.myviewpager);
		indicatorViewGroup = (ViewGroup) mainViewGroup
				.findViewById(R.id.mybottomviewgroup);

		for (int i = 0; i < mImageViews.length; i++) {
			mImageView = new ImageView(ToppickActivity.this);
			mImageView.setLayoutParams(new LayoutParams(20, 20));
			mImageView.setPadding(20, 0, 20, 0);

			if (i == 0) {
				mImageView
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				mImageView.setBackgroundResource(R.drawable.page_indicator);
			}

			mImageViews[i] = mImageView;

			// 把指示作用的远点图片加入底部的视图中
			indicatorViewGroup.addView(mImageViews[i]);
		}

		// 注意这两种用法的区别，前者无法正常显示！！
		// setContentView(R.layout.main);
		setContentView(mainViewGroup);

		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mImageViews.length; i++) {
					if (i == arg0) {
						mImageViews[i]
								.setBackgroundResource(R.drawable.page_indicator_focused);
					} else {
						mImageViews[i]
								.setBackgroundResource(R.drawable.page_indicator);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		Button button = (Button) mainViewGroup.findViewById(R.id.NavigateBack);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		Button button2 = (Button) mainViewGroup.findViewById(R.id.Navigatebuy);
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (PublicContainer.customer.getId() == 0) {
					Intent intent = new Intent(ToppickActivity.this,
							LoginView.class);
					intent.putExtra("action", "ToppickActivity");
					startActivity(intent);
				} else {
					Buy buy = new Buy();
					buy.start();
				}
			}
		});

	}

	class Receive extends Thread {
		@Override
		public void run() {
			super.run();
			book = new Book(0);
			// TODO Auto-generated method stub
			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_BOOKINFO_URL);
			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);
			// 讲验证码，用户名和密码以JSON格式发送给服务端
			try {
				JSONObject params = new JSONObject();
				params.put("bookid", id);
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e1) {
				// showMessage(e1.toString());
			} catch (JSONException e1) {
				// showMessage("信息获取错误！");
			}
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(httpPost);
				String responseBody = EntityUtils
						.toString(response.getEntity());
				Log.d("xiangqing", responseBody);
				JSONObject jsons = new JSONObject(responseBody);
				book.setname(jsons.getString("bookname"));
				book.setprice(jsons.getDouble("price"));
				book.setsalesvolume(jsons.getInt("salesvolume"));

				book.setauthor(jsons.getString("author"));
				book.setpublisher(jsons.getString("publisher"));
				book.setcatalog(jsons.getString("catalog"));
				book.setpagecount(jsons.getInt("pagecount"));
				book.seteditorialrecommend(jsons
						.getString("editorialrecommend"));
				book.setwordcount(jsons.getInt("wordcount"));
				book.setintroduction(jsons.getString("introduction"));
				Log.d("mulu", book.getcatalog());
				showtext(book);

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

	class Receivecomments extends Thread {
		@Override
		public void run() {
			super.run();
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_COMMENTS_URL);
			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);
			// 讲验证码，用户名和密码以JSON格式发送给服务端
			try {
				JSONObject params = new JSONObject();
				params.put("bookid", id);
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e1) {
				// showMessage(e1.toString());
			} catch (JSONException e1) {
				// showMessage("信息获取错误！");
			}

			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(httpPost);
				String responseBody = EntityUtils
						.toString(response.getEntity());
				JSONArray jsonarray = new JSONArray(responseBody);
				counter = jsonarray.length();
				content = new String[counter];
				contentdate = new String[counter];
				for (int i = 0; i < counter; i++) {
					JSONObject json = jsonarray.getJSONObject(i);
					content[i] = json.getString("content");
					contentdate[i] = json.getString("contentdate");
				}
				showcomment(content);
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

	class Buy extends Thread {
		@Override
		public void run() {
			super.run();
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_SHOPPING_URL);
			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
				System.out.println(sessionid);
			}
			httpPost.setParams(httpParameters);
			// 讲验证码，用户名和密码以JSON格式发送给服务端
			try {
				JSONObject params = new JSONObject();
				params.put("bookid", id);
				params.put("op", "add");
				params.put("cusid", PublicContainer.customer.getId());
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps));
			} catch (UnsupportedEncodingException e1) {
				// showMessage(e1.toString());
			} catch (JSONException e1) {
				// showMessage("信息获取错误！");
			}
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(httpPost);
				String responseBody = EntityUtils
						.toString(response.getEntity());
				JSONObject jsons = new JSONObject(responseBody);
				boolean isok = jsons.getBoolean("isok");
				if (isok) {
					showis("添加成功！");
				} else {
					showis("添加失败！");
				}

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

	private void showtext(Book book) {
		Message msg = Message.obtain(handler, settexthandler.SHOW_TEXT);
		msg.obj = book;
		msg.sendToTarget();
	}

	private void showcomment(String[] s) {
		Message msg = Message.obtain(handler, settexthandler.SHOW_COMMENT);
		msg.obj = s;
		msg.sendToTarget();
	}

	private void showis(String s) {
		Message msg = Message.obtain(handler, settexthandler.SHOW_MESSAGE);
		msg.obj = s;
		msg.sendToTarget();
	}

	class settexthandler extends Handler {
		// 显示错误信息
		public static final int SHOW_TEXT = 0x0001;
		public static final int SHOW_COMMENT = 0x0002;
		public static final int SHOW_MESSAGE = 0x0003;

		@SuppressLint("HandlerLeak")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_TEXT) {
				Book b = (Book) msg.obj;
				View v1 = mPageViews.get(0);
				View v2 = mPageViews.get(1);
				TextView toppickcatalog = (TextView) v1
						.findViewById(R.id.toppickcatalog);
				TextView toppickname = (TextView) v2
						.findViewById(R.id.toppickname);
				TextView toppickauthor = (TextView) v2
						.findViewById(R.id.toppickauthor);
				TextView toppickpublisher = (TextView) v2
						.findViewById(R.id.toppickpublisher);
				TextView toppickeditor = (TextView) v2
						.findViewById(R.id.toppickeditor);
				TextView toppickintro = (TextView) v2
						.findViewById(R.id.toppickintro);
				TextView toppickpage = (TextView) v2
						.findViewById(R.id.toppickpage);
				TextView toppickword = (TextView) v2
						.findViewById(R.id.toppickword);
				TextView toppickprice = (TextView) v2
						.findViewById(R.id.toppickprice);
				TextView toppicksales = (TextView) v2
						.findViewById(R.id.toppicksales);
				ImageView toppickimage = (ImageView) v2
						.findViewById(R.id.toppickimage);
				toppickcatalog.setText(b.getcatalog());
				toppickauthor.setText("作者：" + b.getauthor());
				toppickname.setText("图书名：" + b.getname());
				toppickeditor.setText("读者推荐：" + b.geteditorialrecommend());
				toppickintro.setText("简要介绍：" + b.getintroduction());
				toppickpage.setText("总页数：" + String.valueOf(b.getpagecount()));
				toppickprice.setText("价格：" + String.valueOf(b.getprice()));
				toppickpublisher.setText("出版社：" + b.getpublisher());
				toppickword.setText("总字数：" + String.valueOf(b.getwordcount()));
				toppicksales
						.setText("销量：" + String.valueOf(b.getsalesvolume()));
				try {
					toppickimage.setImageBitmap(BitmapFactory
							.decodeStream(ToppickActivity.this
									.getContentResolver().openInputStream(
											imageUri)));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (msg.what == SHOW_COMMENT) {
				String[] comment = (String[]) msg.obj;
				View v3 = mPageViews.get(2);
				for (int i = 0; i < counter; i++) {
					Map<String, Object> book = new HashMap<String, Object>();
					book.put("comment", comment[i]);
					book.put("commentdate", contentdate[i]);
					Log.d("pinglun", comment[i]);
					Log.d("riqi", contentdate[i]);
					bookList.add(book);
				}
				CommentAdapter adapter = new CommentAdapter(bookList,
						ToppickActivity.this);
				ListView list = (ListView) v3
						.findViewById(R.id.toppickcomments);
				list.setAdapter(adapter);

			}
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(ToppickActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
		}

	}

	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

}