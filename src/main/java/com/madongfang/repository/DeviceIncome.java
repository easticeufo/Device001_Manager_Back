package com.madongfang.repository;

public class DeviceIncome {

	public DeviceIncome() {
		super();
	}

	public DeviceIncome(String deviceCode, Long income) {
		super();
		this.deviceCode = deviceCode;
		this.income = income;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public Long getIncome() {
		return income;
	}

	public void setIncome(Long income) {
		this.income = income;
	}

	@Override
	public String toString() {
		return "DeviceIncome [deviceCode=" + deviceCode + ", income=" + income + "]";
	}

	private String deviceCode;
	
	private Long income;
}
