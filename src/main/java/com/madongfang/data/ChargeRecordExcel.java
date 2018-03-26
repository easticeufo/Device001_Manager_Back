package com.madongfang.data;

public class ChargeRecordExcel {

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getDeviceArea() {
		return deviceArea;
	}

	public void setDeviceArea(String deviceArea) {
		this.deviceArea = deviceArea;
	}

	public String getDeviceLocation() {
		return deviceLocation;
	}

	public void setDeviceLocation(String deviceLocation) {
		this.deviceLocation = deviceLocation;
	}

	public Integer getPlugId() {
		return plugId;
	}

	public void setPlugId(Integer plugId) {
		this.plugId = plugId;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getStopTime() {
		return stopTime;
	}

	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Float getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(Float powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public Integer getCustomId() {
		return customId;
	}

	public void setCustomId(Integer customId) {
		this.customId = customId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	private String deviceCode;
	
	private String deviceArea;
	
	private String deviceLocation;
	
	private Integer plugId;
	
	private String startTime;
	
	private String stopTime;
	
	private Integer duration;
	
	private Float powerConsumption;
	
	private Float amount;
	
	private Integer customId;
	
	private String nickname;
}
