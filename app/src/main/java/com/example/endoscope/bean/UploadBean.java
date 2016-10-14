package com.example.endoscope.bean;


//上传信息类
public class UploadBean {
	private String uploadfile;
	private int num;

	public UploadBean() {

	}

	public UploadBean(String uploadfile, int num) {
		super();
		this.uploadfile = uploadfile;
		this.num = num;
	}

	public void setuploadfile(String uploadfile) {
		this.uploadfile = uploadfile;
	}

	public String getuploadfile() {
		return uploadfile;
	}

	public void setnum(int num) {
		this.num = num;
	}

	public int getnum() {
		return num;
	}

}
