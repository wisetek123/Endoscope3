package com.example.endoscope.bean;

//病人信息类
public class PatientBean {

	private String no;
	private String name;
	private String sex;
	private String age;
	private String tel;
	private String adr;
	private String IDnum;

	public PatientBean() {
		// TODO Auto-generated constructor stub
	}

	public PatientBean(String no, String name, String sex, String age,
			String tel, String adr, String IDnum) {
		super();
		this.no = no;
		this.name = name;
		this.sex = sex;
		this.age = age;
		this.tel = tel;
		this.adr = adr;
		this.IDnum = IDnum;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getAdr() {
		return adr;
	}

	public void setAdr(String adr) {
		this.adr = adr;
	}

	public String getIDnum() {
		return IDnum;
	}

	public void setIDnum(String iDnum) {
		IDnum = iDnum;
	}

}
