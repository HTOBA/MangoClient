package com.example.grallery3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.mangoclient.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageAdapter_1 extends BaseAdapter {
	private ImageView[] mImages; // 保存倒影图片的数组

	private Context mContext;
	public List<Map<String, Object>> list;

	public Integer[] imgs = { R.drawable.newimage01, R.drawable.newimage02,
			R.drawable.newimage03, R.drawable.newimage04, R.drawable.newimage05, R.drawable.newimage06 };
	public String[] titles = { "Java核心技术：卷Ⅰ基础知识",
							"JavaScript权威指南(第五版)",
							"Java编程思想（第4版）",
							"轻量级Java EE企业应用实战（第3版）", 
							"Photoshop数码相片处理技巧大全" ,
							"中文版Photoshop CS5完全自学教程"};

	public ImageAdapter_1(Context c) {
		this.mContext = c;
		list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < imgs.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("image", imgs[i]);
			list.add(map);
		}
		mImages = new ImageView[list.size()];
	}

	/** 反射倒影 */
	public boolean createReflectedImages() {
		final int reflectionGap = 4;
		final int Height = 200;
		int index = 0;
		for (Map<String, Object> map : list) {
			Integer id = (Integer) map.get("image");
			// 获取原始图片s
			Bitmap originalImage = BitmapFactory.decodeResource(
					mContext.getResources(), id);
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			float scale = Height / (float) height;

			Matrix sMatrix = new Matrix();
			sMatrix.postScale(scale, scale);
			Bitmap miniBitmap = Bitmap.createBitmap(originalImage, 0, 0,
					originalImage.getWidth(), originalImage.getHeight(),
					sMatrix, true);

			// 是否原图片数据，节省内存
			originalImage.recycle();

			int mwidth = miniBitmap.getWidth();
			int mheight = miniBitmap.getHeight();
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1); // 图片矩阵变换（从低部向顶部的倒影）
			Bitmap reflectionImage = Bitmap.createBitmap(miniBitmap, 0,
					mheight / 2, mwidth, mheight / 2, matrix, false); // 截取原图下半部分
			Bitmap bitmapWithReflection = Bitmap.createBitmap(mwidth,
					(mheight + mheight / 2), Config.ARGB_8888); // 创建倒影图片（高度为原图3/2）

			Canvas canvas = new Canvas(bitmapWithReflection); // 绘制倒影图（原图 + 间距 +
																// 倒影）
			canvas.drawBitmap(miniBitmap, 0, 0, null); // 绘制原图
			Paint paint = new Paint();
			canvas.drawRect(0, mheight, mwidth, mheight + reflectionGap, paint); // 绘制原图与倒影的间距
			canvas.drawBitmap(reflectionImage, 0, mheight + reflectionGap, null); // 绘制倒影图

			paint = new Paint();
			LinearGradient shader = new LinearGradient(0,
					miniBitmap.getHeight(), 0, bitmapWithReflection.getHeight()
							+ reflectionGap, 0x70ffffff, 0x00ffffff,
					TileMode.CLAMP);
			paint.setShader(shader); // 线性渐变效果
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN)); // 倒影遮罩效果
			canvas.drawRect(0, mheight, mwidth,
					bitmapWithReflection.getHeight() + reflectionGap, paint); // 绘制倒影的阴影效果

			ImageView imageView = new ImageView(mContext);
			imageView.setImageBitmap(bitmapWithReflection); // 设置倒影图片
			imageView.setLayoutParams(new GalleryView.LayoutParams(
					(int) (width * scale),
					(int) (mheight * 3 / 2.0 + reflectionGap)));
			imageView.setScaleType(ScaleType.MATRIX);
			mImages[index++] = imageView;
		}
		return true;
	}

	@Override
	public int getCount() {
		return imgs.length;
	}

	@Override
	public Object getItem(int position) {
		return mImages[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return mImages[position]; // 显示倒影图片（当前获取焦点）
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

}
