package com.madongfang.api;

import java.util.Date;

public class ChargeRecordApi {

	public Integer getPlugId() {
		return plugId;
	}

	public void setPlugId(Integer plugId) {
		this.plugId = plugId;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getStopTime() {
		return stopTime;
	}

	public void setStopTime(Date stopTime) {
		this.stopTime = stopTime;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(Integer powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

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

	private Integer plugId;
	
	private Date startTime;
	
	private Date stopTime;
	
	private Integer amount;
	
	private Integer powerConsumption;
	
	private String deviceCode;
	
	private String deviceArea;
	
	private String deviceLocation;
	
	private Integer customId;
	
	private String nickname;
}
