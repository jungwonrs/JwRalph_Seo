package com.example.tests;

import javax.servlet.http.HttpServletRequest;


public class Request {

	private String DLNum;
	private String SNum;
	
	public Request(HttpServletRequest request) {
		this.DLNum = request.getParameter("driveLicenseNumber");
		this.SNum = request.getParameter("SerialNumber");
	}

	public String getDLNum() {
		return DLNum;
	}

	public void setDLNum(String dLNum) {
		this.DLNum = dLNum;
	}

	public String getSNum() {
		return SNum;
	}

	public void setSNum(String sNum) {
		this.SNum = sNum;
	}
	
	
}
