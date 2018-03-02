package com.madongfang.repository;

import java.util.Date;

public class ReportRecord {

	public ReportRecord() {
		super();
	}

	public ReportRecord(Date startTime, Date stopTime, Integer startAmount, Integer stopAmount,
			Integer powerConsumption) {
		super();
		this.startTime = startTime;
		this.stopTime = stopTime;
		this.startAmount = startAmount;
		this.stopAmount = stopAmount;
		this.powerConsumption = powerConsumption;
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

	private Date startTime;
	
	private Date stopTime;
	
	private Integer startAmount;
	
	private Integer stopAmount;
	
	private Integer powerConsumption;
}
