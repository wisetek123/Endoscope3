package com.example.endoscope.bean;

import android.graphics.Bitmap;

//诊断信息类
public class DiagnosisBean {

	private String date;
	private String symptom;
	private String result;
	private String image;

	public DiagnosisBean() {
		// TODO Auto-generated constructor stub
	}

	public DiagnosisBean(String date, String symptom, String result) {
		super();
		this.date = date;
		this.symptom = symptom;
		this.result = result;

	}

	public DiagnosisBean(String date, String symptom, String result,
			String image) {
		super();
		this.date = date;
		this.symptom = symptom;
		this.result = result;
		this.image = image;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getSymptom() {
		return symptom;
	}

	public void setSymptom(String symptom) {
		this.symptom = symptom;
	}

	public String getResult() {
		return result;
	}

	public String getImage() {
		return image;
	}

	public void setImage() {
		this.image = image;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
