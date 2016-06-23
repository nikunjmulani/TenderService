package com.tender.beans;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Complain")
public class Complain {
	private String billYear;
	private String billMonth;
	private int complainId;
	private String complainCode;
	private String complainDesc;
	private List<Item> itemList;

	public String getBillYear() {
		return billYear;
	}

	public void setBillYear(String billYear) {
		this.billYear = billYear;
	}

	public String getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(String billMonth) {
		this.billMonth = billMonth;
	}

	public int getComplainId() {
		return complainId;
	}

	public void setComplainId(int complainId) {
		this.complainId = complainId;
	}

	public String getComplainCode() {
		return complainCode;
	}

	public void setComplainCode(String complainCode) {
		this.complainCode = complainCode;
	}

	public String getComplainDesc() {
		return complainDesc;
	}

	public void setComplainDesc(String complainDesc) {
		this.complainDesc = complainDesc;
	}

	public List<Item> getItemList() {
		return itemList;
	}

	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
}
