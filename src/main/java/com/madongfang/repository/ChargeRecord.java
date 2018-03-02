package com.madongfang.repository;

import java.util.Date;

public class ChargeRecord {

	public ChargeRecord() {
		super();
	}

	public ChargeRecord(Integer plugId, Date startTime, Date stopTime, Integer startAmount, Integer stopAmount,
			Integer powerConsumption, String deviceCode, String deviceArea, String deviceLocation, Integer customId,
			String nickname) {
		super();
		this.plugId = plugId;
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.startAmount = startAmount;
		this.stopAmount = stopAmount;
		this.powerConsumption = powerConsumption;
		this.deviceCode = deviceCode;
		this.deviceArea = deviceArea;
		this.deviceLocation = deviceLocation;
		this.customId = customId;
		this.nickname = nickname;
	}

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

	public Integer getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(Integer startAmount) {
		this.startAmount = startAmount;
	}

	public Integer getStopAmount() {
		return stopAmount;
	}

	public void setStopAmount(Integer stopAmount) {
		this.stopAmount = stopAmount;
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
	
	private Integer startAmount;
	
	private Integer stopAmount;
	
	private Integer powerConsumption;

	private String deviceCode;
	
	private String deviceArea;
	
	private String deviceLocation;
	
	private Integer customId;
	
	private String nickname;
}
