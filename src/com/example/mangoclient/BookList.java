package com.example.mangoclient;

import android.os.Parcel;
import android.os.Parcelable;

public class BookList implements Parcelable {
	private String uri = "";
	private String url = "";
	private String name = "";
	private Double price = 0.0;
	private int quantity = 1;
	private boolean checked = false;
	private int bookid = 0;

	public BookList(String uri, String url, String name, Double price,
			boolean checked, int bookid) {
		this.uri = uri;
		this.url = url;
		this.name = name;
		this.price = price;
		this.checked = checked;
		this.bookid = bookid;
	}

	public BookList() {
	}

	/**
	 * @return the image
	 */
	public String getUri() {
		return uri;
	}

	public String getUrl() {
		return url;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the price
	 */
	public Double getPrice() {
		return price;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getBookid() {
		return bookid;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeString(uri);
		parcel.writeString(name);
		parcel.writeDouble(price);
		parcel.writeByte((byte) (checked ? 1 : 0));
		parcel.writeInt(bookid);
		parcel.writeInt(quantity);
	}

	public static final Parcelable.Creator<BookList> CREATOR = new Creator<BookList>() {

		@Override
		public BookList createFromParcel(Parcel source) {
			BookList bl = new BookList();
			bl.uri = source.readString();
			bl.name = source.readString();
			bl.price = source.readDouble();
			bl.checked = source.readByte() != 0;
			bl.bookid = source.readInt();
			bl.quantity = source.readInt();
			return bl;
		}

		@Override
		public BookList[] newArray(int size) {
			return new BookList[size];
		}
	};
}
