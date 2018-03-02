package com.madongfang.data;

public class ReportExcel {

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getChargeNumber() {
		return chargeNumber;
	}

	public void setChargeNumber(Integer chargeNumber) {
		this.chargeNumber = chargeNumber;
	}

	public String getDurationTime() {
		return durationTime;
	}

	public void setDurationTime(String durationTime) {
		this.durationTime = durationTime;
	}

	public Float getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(Float powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public Float getIncome() {
		return income;
	}

	public void setIncome(Float income) {
		this.income = income;
	}

	private String time;
	
	private Integer chargeNumber;
	
	private String durationTime;
	
	private Float powerConsumption;
	
	private Float income;
}
