package com.tender.beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "item")
public class Item {

	private int index;
	private int itemId;
	private int itemGroupId;
	private String itemName;
	private double rate;
	private int unit;
	private int itemQuantity;
	private double amount;

	public int getItemQuantity() {
		return itemQuantity;
	}

	public void setItemQuantity(int itemQuantity) {
		this.itemQuantity = itemQuantity;
	}

	public Item() {
	}

	public Item(int itemId, int itemGroupId, String itemName, double rate, int unit) {
		this.itemId = itemId;
		this.itemGroupId = itemGroupId;
		this.itemName = itemName;
		this.rate = rate;
		this.unit = unit;
	}

	public Item(String itemName) {
		this.itemName = itemName;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getItemGroupId() {
		return itemGroupId;
	}

	public void setItemGroupId(int itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public int getUnit() {
		return unit;
	}

	public void setUnit(int unit) {
		this.unit = unit;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
