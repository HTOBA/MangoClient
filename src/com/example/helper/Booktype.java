package com.example.helper;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Booktype entity. @author MyEclipse Persistence Tools
 */

public class Booktype implements java.io.Serializable {

	private static final long serialVersionUID = 5679137267629535336L;

	private String typeid;
	private String typename;
	private String parents;
	private Timestamp version;
	private String operator;
	private List<Book> books = new ArrayList<Book>(0);

	/** default constructor */
	public Booktype() {
	}

	/** minimal constructor */
	public Booktype(String typename, String parents) {
		this.typename = typename;
		this.parents = parents;
	}

	// Property accessors

	public String getTypeid() {
		return this.typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getTypename() {
		return this.typename;
	}

	public void setTypename(String typename) {
		this.typename = typename;
	}

	public String getParents() {
		return this.parents;
	}

	public void setParents(String parents) {
		this.parents = parents;
	}

	public Timestamp getVersion() {
		return this.version;
	}

	public void setVersion(Timestamp version) {
		this.version = version;
	}

	public String getOperator() {
		return this.operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
	}
}