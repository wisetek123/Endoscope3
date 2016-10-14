package com.example.endoscope.bean;

//图片信息类
public class BrowseListBean {

	private String name;
	private String FilePath;

	public BrowseListBean() {
		// TODO Auto-generated constructor stub
	}

	public BrowseListBean(String name, String FilePath) {
		super();
		this.name = name;
		this.FilePath = FilePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePath() {
		return FilePath;
	}

	public void setFilePath(String FilePath) {
		this.FilePath = FilePath;
	}

}
