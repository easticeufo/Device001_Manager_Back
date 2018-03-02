package com.madongfang.api;

import java.util.List;

public class DeviceBatchApi {

	public List<String> getDeviceCodes() {
		return deviceCodes;
	}

	public void setDeviceCodes(List<String> deviceCodes) {
		this.deviceCodes = deviceCodes;
	}

	public Integer getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Integer unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Integer minPrice) {
		this.minPrice = minPrice;
	}

	public Integer getAttachPrice() {
		return attachPrice;
	}

	public void setAttachPrice(Integer attachPrice) {
		this.attachPrice = attachPrice;
	}

	public Integer getOverdraft() {
		return overdraft;
	}

	public void setOverdraft(Integer overdraft) {
		this.overdraft = overdraft;
	}

	public Integer getFloatChargeTime() {
		return floatChargeTime;
	}

	public void setFloatChargeTime(Integer floatChargeTime) {
		this.floatChargeTime = floatChargeTime;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	private List<String> deviceCodes;
	
	private Integer unitPrice; // 单位电价，单位：分/度
	
	private Integer minPrice; // 最低电费，单位：分，当实际消耗的电量计算所得的电费小于最低电费时，按照最低电费计算电费
	
	private Integer attachPrice; // 附加收费，单位：分，每次充电的附加使用费，即表示在电费基础上多收取的费用
	
	private Integer overdraft; // 透支系数，1000表示不透支
	
	private Integer floatChargeTime; // 浮充充电时间，单位：分钟
	
	private Integer areaId;
}
