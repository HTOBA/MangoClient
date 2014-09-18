package com.example.adapter;
import java.io.FileNotFoundException;
import java.util.List;

import com.example.helper.GridItem;
import com.example.mangoclient.R;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridViewAdapter extends BaseAdapter {

	private List<GridItem> datas = null;
	private Context context = null;


	public GridViewAdapter(List<GridItem> datas,Context context) {
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

	public View getView(int position, View convertView, ViewGroup parent) {
		final GridItem book = datas.get(position);
		ViewHolder holder = null;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.imageview, null);
			holder = new ViewHolder();
			holder.bookname = (TextView) convertView.findViewById(R.id.bookname);
			holder.bookimage = (ImageView) convertView.findViewById(R.id.image);
			holder.salesvolume = (TextView) convertView.findViewById(R.id.salevolume);
			holder.book_price = (TextView) convertView.findViewById(R.id.bookprice);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder) convertView.getTag();
		}
		try {
			holder.bookimage.setImageBitmap(BitmapFactory.decodeStream(context.getContentResolver().openInputStream(book.getimageuri())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		holder.bookname.setText(book.getname());
		holder.salesvolume.setText("销量：" + book.getsalevolume());
		holder.book_price.setText("价格："+String.valueOf(book.getprice()));
		return convertView;
	}

	class ViewHolder {
		public ImageView bookimage = null;
		public TextView salesvolume = null;
		public TextView bookname = null;
		public TextView book_price = null;
	}
}
