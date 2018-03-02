package com.madongfang.api;

public class ManagerSplitApi {

	public ManagerSplitApi(String managerNickname, String managerRemark, Long splitAmount) {
		super();
		this.managerNickname = managerNickname;
		this.managerRemark = managerRemark;
		this.splitAmount = splitAmount;
	}

	public String getManagerNickname() {
		return managerNickname;
	}

	public void setManagerNickname(String managerNickname) {
		this.managerNickname = managerNickname;
	}

	public String getManagerRemark() {
		return managerRemark;
	}

	public void setManagerRemark(String managerRemark) {
		this.managerRemark = managerRemark;
	}

	public Long getSplitAmount() {
		return splitAmount;
	}

	public void setSplitAmount(Long splitAmount) {
		this.splitAmount = splitAmount;
	}

	private String managerNickname;
	
	private String managerRemark;
	
	private Long splitAmount;
}
