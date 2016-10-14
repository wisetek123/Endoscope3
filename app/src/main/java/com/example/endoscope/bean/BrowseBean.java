package com.example.endoscope.bean;

//目录信息类
public class BrowseBean {

	private String type;
	private String num;

	public BrowseBean() {
		// TODO Auto-generated constructor stub
	}

	public BrowseBean(String type, String num) {
		super();
		this.type = type;
		this.num = num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

}
