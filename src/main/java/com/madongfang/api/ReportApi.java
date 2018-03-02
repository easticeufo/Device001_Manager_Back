package com.madongfang.api;

import java.util.Date;

public class ReportApi {

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public int getChargeNumber() {
		return chargeNumber;
	}

	public void setChargeNumber(int chargeNumber) {
		this.chargeNumber = chargeNumber;
	}

	public int getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(int durationTime) {
		this.durationTime = durationTime;
	}

	public int getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(int powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public int getIncome() {
		return income;
	}

	public void setIncome(int income) {
		this.income = income;
	}

	private String type; // "year" or "month" or "date"
	
	private Date startTime;
	
	private Date stopTime;
	
	private int chargeNumber;
	
	private int durationTime; // 充电时长，单位：秒
	
	private int powerConsumption;
	
	private int income;
}
