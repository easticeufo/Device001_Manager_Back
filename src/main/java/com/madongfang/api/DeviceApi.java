package com.madongfang.api;

public class DeviceApi {

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getPlugNumber() {
		return plugNumber;
	}

	public void setPlugNumber(Integer plugNumber) {
		this.plugNumber = plugNumber;
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

	public Integer getFactor() {
		return factor;
	}

	public void setFactor(Integer factor) {
		this.factor = factor;
	}

	public Integer getMaxPlugPower() {
		return maxPlugPower;
	}

	public void setMaxPlugPower(Integer maxPlugPower) {
		this.maxPlugPower = maxPlugPower;
	}

	public Integer getMaxDevicePower() {
		return maxDevicePower;
	}

	public void setMaxDevicePower(Integer maxDevicePower) {
		this.maxDevicePower = maxDevicePower;
	}

	public Integer getFloatChargeTime() {
		return floatChargeTime;
	}

	public void setFloatChargeTime(Integer floatChargeTime) {
		this.floatChargeTime = floatChargeTime;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getInusePlugNumber() {
		return inusePlugNumber;
	}

	public void setInusePlugNumber(Integer inusePlugNumber) {
		this.inusePlugNumber = inusePlugNumber;
	}

	public Boolean getIsOnline() {
		return isOnline;
	}

	public void setIsOnline(Boolean isOnline) {
		this.isOnline = isOnline;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String code;
	
	private String id;
	
	private Integer plugNumber;
	
	private Integer unitPrice; // 单位电价，单位：分/度
	
	private Integer minPrice; // 最低电费，单位：分，当实际消耗的电量计算所得的电费小于最低电费时，按照最低电费计算电费
	
	private Integer attachPrice; // 附加收费，单位：分，每次充电的附加使用费，即表示在电费基础上多收取的费用
	
	private Integer overdraft; // 透支系数，1000表示不透支
	
	private Integer factor; // 校准因子
	
	private Integer maxPlugPower;
	
	private Integer maxDevicePower;
	
	private Integer floatChargeTime; // 浮充充电时间，单位：分钟
	
	private String area;
	
	private Integer areaId;
	
	private String location;
	
	private Integer inusePlugNumber;
	
	private Boolean isOnline;
	
	private String type;
}
