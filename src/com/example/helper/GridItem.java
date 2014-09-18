package com.example.helper;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class GridItem {
	private Uri imageuri;
	private double price;
	private String name;
	private int salesvolume;
	private String imagename;
	private String picdirpath = "sdcard/"+ "imagebook/";
	private Context context;
	public GridItem(Uri imageuri,String name,double price,int salevolume,Context context) {
		this.imageuri=imageuri;
		this.name=name;
		this.price=price;
		this.salesvolume=salevolume;
		this.context=context;
	}
	public Uri getimageuri(){
		return imageuri;
	}
	public String getname(){
		return name;
	}
	public double getprice(){
		return price;
	}
	public int getsalevolume(){
		return salesvolume;
	}
	public String getpath(){
		return picdirpath+imagename;
		
	}	
}
