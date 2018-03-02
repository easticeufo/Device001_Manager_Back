package com.madongfang.data;

public class ManagerSplit {

	public ManagerSplit() {
		super();
	}

	public ManagerSplit(Integer managerId, String openId) {
		super();
		this.managerId = managerId;
		this.openId = openId;
		this.splitAmount = 0;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Integer getSplitAmount() {
		return splitAmount;
	}

	public void setSplitAmount(Integer splitAmount) {
		this.splitAmount = splitAmount;
	}

	private Integer managerId;
	
	private String openId;
	
	private Integer splitAmount;
}
