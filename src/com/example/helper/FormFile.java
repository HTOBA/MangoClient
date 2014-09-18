/**      
 * <p>project_name��contacts</p> 
 * <p>Description: </p> 
 * <p>Copyright: Copyright (c) 2011 by tl3shi.</p> 
 **/

package com.example.helper;

/**
 * <p>
 * Title: FormFile.java
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author <a href="mailto:tanglei3shi@163.com">Administrator</a>
 * @date 2011-3-3 ����03:25:44
 * @version 1.0
 */
public class FormFile {

	/* �ϴ��ļ������� */
	private byte[] data;

	/**
	 * @return the data
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/* �ļ����� */
	private String filname;
	/* ���ֶ����� */
	private String formname;
	/* �������� */
	private String contentType = "application/octet-stream"; // ��Ҫ������ص�����

	public FormFile(String filname, byte[] data, String formname,
			String contentType) {
		this.data = data;
		this.filname = filname;
		this.formname = formname;
		if (contentType != null)
			this.contentType = contentType;
	}

	/**
	 * @return the filname
	 */
	public String getFilname() {
		return filname;
	}

	/**
	 * @param filname
	 *            the filname to set
	 */
	public void setFilname(String filname) {
		this.filname = filname;
	}

	/**
	 * @return the formname
	 */
	public String getFormname() {
		return formname;
	}

	/**
	 * @param formname
	 *            the formname to set
	 */
	public void setFormname(String formname) {
		this.formname = formname;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @param contentType
	 *            the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
