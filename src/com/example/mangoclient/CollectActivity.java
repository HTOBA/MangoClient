package com.example.mangoclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.adapter.CommentAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class CollectActivity extends Activity {

	private String[] book_name = new String[] { "小王子", "围城", "活着" };
	private int[] book_image = new int[] { R.drawable.book1, R.drawable.book2,
			R.drawable.book3 };
	private String[] book_price = new String[] { "19.8", "29.8", "99.8" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acticity_collect);
		List<Map<String, Object>> bookList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < book_name.length; i++) {
			Map<String, Object> book = new HashMap<String, Object>();
			book.put("name", book_name[i]);
			book.put("image", book_image[i]);
			book.put("price", book_price[i]);
			bookList.add(book);
		}
		CommentAdapter adapter = new CommentAdapter(bookList, this);
		ListView payList = (ListView) findViewById(R.id.collect_listview);
		payList.setAdapter(adapter);
	}
}
