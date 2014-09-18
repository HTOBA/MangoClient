package com.example.mangoclient;

import java.io.File;
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
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.GridViewAdapter;
import com.example.helper.Book;
import com.example.helper.GridItem;
import com.example.helper.HttpDownloader;
import com.example.helper.PublicContainer;

public class MainActivity extends Activity {
	private String path;
	private int counter;
	private String download;
	private GridView gridview;
	private ImageView imagview;
	private String sessionid = null;
	private gridViewhandler handler = null;
	private Book[] book;
	private TextView edit = null;
	private String picdirpath = "sdcard/"+ "imagebook/";
	boolean sdCardExist = Environment.getExternalStorageState().equals(
			android.os.Environment.MEDIA_MOUNTED);
	private File imagefile;
	private List<GridItem> bookgrids = new ArrayList<GridItem>();
	private GridViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		gridview = (GridView) findViewById(R.id.gridview);
		handler = new gridViewhandler();
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {	
				int i=(int) gridview.getItemIdAtPosition(arg2);
				Intent intent=new Intent(MainActivity.this,ToppickActivity.class);
				intent.putExtra("id",i+1);
				intent.putExtra("imagename", book[i].getimagesname().toString());
				intent.putExtra("picdirpath", picdirpath);
				startActivity(intent);
			}
		});
		Button btn_shop = (Button) findViewById(R.id.shopcar);
		btn_shop.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (PublicContainer.customer.getId() == 0) {
					Intent intent = new Intent(MainActivity.this,
							LoginView.class);
					intent.putExtra("action","ShopActivity");
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							ShopActivity.class);
					startActivity(intent);
				}
			}
		});
		
		Button info_btn = (Button) findViewById(R.id.information);
		info_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (PublicContainer.customer.getId() == 0) {
					Intent intent = new Intent(MainActivity.this,
							LoginView.class);
					intent.putExtra("action","MyInfoActivity");
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							MyInfoActivity.class);
					startActivity(intent);
				}
			}
		});
		
		Button order = (Button)findViewById(R.id.ordergoods);
		order.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View arg0) {
				if (PublicContainer.customer.getId() == 0) {
					Intent intent = new Intent(MainActivity.this,
							LoginView.class);
					intent.putExtra("action","OrderActivity");
					startActivity(intent);
				} else {
					Intent intent = new Intent(MainActivity.this,
							OrderActivity.class);
					startActivity(intent);
				}
			}
		});
			
		Button price=(Button) findViewById(R.id.price);
		price.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				bookgrids.removeAll(bookgrids);
				adapter.notifyDataSetChanged();
				pricesort();
				for(int i=0;i<counter;i++){
					GridItem bookitem = new GridItem(book[i].getimageUri(), book[i].getname(),
							book[i].getprice(), book[i].getsalesvolume(),MainActivity.this);
					bookgrids.add(bookitem);	
				}
				adapter = new GridViewAdapter(bookgrids, MainActivity.this);
				showPicture(adapter);
				// TODO Auto-generated method stub	
			}
		});
		
		Button count=(Button) findViewById(R.id.count);
		count.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				bookgrids.removeAll(bookgrids);
				adapter.notifyDataSetChanged();
				salevolumesort();
				for(int i=0;i<counter;i++){
					GridItem bookitem = new GridItem(book[i].getimageUri(), book[i].getname(),
							book[i].getprice(), book[i].getsalesvolume(),MainActivity.this);
					bookgrids.add(bookitem);		
				}
				adapter = new GridViewAdapter(bookgrids, MainActivity.this);
				showPicture(adapter);
				// TODO Auto-generated method stub
				
			}
		});	
		Button home=(Button) findViewById(R.id.home);
		home.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
				//Intent intent =new Intent(MainActivity.this,MenuActivity.class);
				//startActivity(intent);
			}
		});
		Button refresh=(Button) findViewById(R.id.refresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				bookgrids.removeAll(bookgrids);
				adapter.notifyDataSetChanged();
				bookgrid book = new bookgrid();
				book.start();
			}
		});
		
		edit=(TextView) findViewById(R.id.sehgoods);
		Button btn_search=(Button) findViewById(R.id.search);
		btn_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				bookgrids.removeAll(bookgrids);
				adapter.notifyDataSetChanged();
				Search search = new Search();
				search.start();
				adapter = new GridViewAdapter(bookgrids, MainActivity.this);
				showPicture(adapter);
			}
		});
		bookgrid book = new bookgrid();
		book.start();
}

	class bookgrid extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_toppick_URL);

			/* 设置请求参数：超时时间 */
			BasicHttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 5 * 1000);
			// 设置sessionid，保存服务端session空间有效,初次发送请求无需设置
			if (sessionid != null) {
				httpPost.setHeader("cookie", "JSESSIONID=" + sessionid);
			}
			httpPost.setParams(httpParameters);

			// 创建一个客户端请求发送器
			HttpClient client = new DefaultHttpClient();
			// 发送Get请求，并等待服务端的响应

			HttpResponse response;
			try {
				response = client.execute(httpPost);
				String responseBody = EntityUtils
						.toString(response.getEntity());
				JSONArray jsonarray = new JSONArray(responseBody);
				counter = jsonarray.length();
				book =new Book[counter];
				for(int i=0;i<counter;i++){
					book[i]=new Book(0);
				}
				for (int i = 0; i < counter; i++) {
					JSONObject json = jsonarray.getJSONObject(i);
					book[i].setname(json.getString("name"));
					book[i].setprice(json.getDouble("price"));
					book[i].setid(json.getInt("id"));
					book[i].setsalesvolume(json.getInt("salesvolume"));
					book[i].setimages(NetworkUtils.DANGDANG_BASE_URL
							+ json.getString("imagepath")+json.getString("imagename"));
					book[i].setimagesname(json.getString("imagename"));
				}
				if (!sdCardExist) {
					Toast.makeText(MainActivity.this, "请插入外部SD存储卡",
							Toast.LENGTH_SHORT).show();
				} else {
					File dirFirstFile = new File(picdirpath);
					if (!dirFirstFile.exists()) {// 判断文件夹目录是否存在
						dirFirstFile.mkdir();// 如果不存在则创建
					}
				}
				for (int i = 0; i < counter; i++) {
					imagefile = new File(picdirpath, book[i].getimagesname());
					book[i].setimageUri(Uri.fromFile(imagefile));
					GridItem bookitem = new GridItem(book[i].getimageUri(), book[i].getname(),
							book[i].getprice(), book[i].getsalesvolume(),MainActivity.this);
					bookgrids.add(bookitem);
					if (!imagefile.exists() && !(imagefile.length() > 0)){
						download = book[i].getimages();
						Download down = new Download();
						down.start();
					}
				}
				adapter = new GridViewAdapter(bookgrids, MainActivity.this);
				showPicture(adapter);
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

	class Search extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			// 创建一个Post请求参数对象，所有需要通过Post方法发送到服务端的请求头和请求体的参数都设置该对象中
			HttpPost httpPost = new HttpPost(NetworkUtils.DANGDANG_SEARCH_URL);
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
				params.put("keyword", edit.getText().toString());
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				BasicNameValuePair se = new BasicNameValuePair("params",
						params.toString());
				nvps.add(se);
				httpPost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				showMessage(e1.toString());
			} catch (JSONException e1) {
				showMessage("信息获取错误！");
			}

			// 创建一个客户端请求发送器
			HttpClient client = new DefaultHttpClient();
			try {
				HttpResponse response = client.execute(httpPost);
				String responseBody = EntityUtils
						.toString(response.getEntity());
				JSONArray jsonarray = new JSONArray(responseBody);
				counter = jsonarray.length();
				book =new Book[counter];
				for(int i=0;i<counter;i++){
					book[i]=new Book(0);
				}
				for (int i = 0; i < counter; i++) {
					JSONObject json = jsonarray.getJSONObject(i);
					book[i].setname(json.getString("name"));
					book[i].setprice(json.getDouble("price"));
					book[i].setid(json.getInt("id"));
					book[i].setsalesvolume(json.getInt("salesvolume"));
					book[i].setimages(NetworkUtils.DANGDANG_BASE_URL
							+ json.getString("imagepath")+json.getString("imagename"));
					book[i].setimagesname(json.getString("imagename"));
				}
				for (int i = 0; i < counter; i++) {
					imagefile = new File(picdirpath, book[i].getimagesname());
					book[i].setimageUri(Uri.fromFile(imagefile));
					GridItem bookitem = new GridItem(book[i].getimageUri(), book[i].getname(),
							book[i].getprice(), book[i].getsalesvolume(),MainActivity.this);
					bookgrids.add(bookitem);
					if (!imagefile.exists() && !(imagefile.length() > 0)){
						download = book[i].getimages();
						Download down = new Download();
						down.start();
					}
				}
				adapter = new GridViewAdapter(bookgrids, MainActivity.this);
				showPicture(adapter);
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
	public void pricesort(){
		int []count=new int[counter];
        for (int i = 0; i < counter-1; i++) {  
            for (int j = 0; j < counter-1-i; j++) {  
                if (book[j].getprice()>book[j + 1].getprice()) {  
                	Book temp=new Book(0);
                	temp=book[j+1];
                	book[j+1]=book[j];
                	book[j]=temp;
                }  
            }  
        } 	
	}
	public void salevolumesort(){
		int []count=new int[counter];
        for (int i = 0; i < counter-1; i++) {  
            for (int j = 0; j < counter-1-i; j++) {  
                if (book[j].getsalesvolume()<book[j + 1].getsalesvolume()) {  
                	Book temp=new Book(0);
                	temp=book[j+1];
                	book[j+1]=book[j];
                	book[j]=temp;
                }  
            }  
        } 
		
	}
	class Download extends Thread {
		public void run() {
			HttpDownloader downloader = new HttpDownloader();
			path = downloader.download(download, picdirpath.substring(7));
		}
	}

	private void showMessage(String message) {
		Message msg = Message.obtain(handler, gridViewhandler.SHOW_MESSAGE);
		msg.obj = message;
		msg.sendToTarget();
	}

	private void showPicture(GridViewAdapter adapter) {
		Message msg = Message.obtain(handler, gridViewhandler.SHOW_PICTEURE);
		msg.obj = adapter;
		msg.sendToTarget();
	}

	class gridViewhandler extends Handler {
		// 显示错误信息
		public static final int SHOW_MESSAGE = 0x0002;
		public static final int SHOW_PICTEURE = 0x0001;

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == SHOW_MESSAGE) {
				Toast.makeText(MainActivity.this, msg.obj.toString(),
						Toast.LENGTH_LONG).show();
			}
			if (msg.what == SHOW_PICTEURE) {
				GridViewAdapter adapter = (GridViewAdapter) msg.obj;
				gridview.setAdapter(adapter);

			}
		}

	}

}
