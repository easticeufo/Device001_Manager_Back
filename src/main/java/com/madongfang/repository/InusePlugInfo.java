package com.madongfang.repository;

public class InusePlugInfo {

	public InusePlugInfo() {
		super();
	}

	public InusePlugInfo(Long number, Long consumePower) {
		super();
		this.number = number;
		this.consumePower = consumePower;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public Long getConsumePower() {
		return consumePower;
	}

	public void setConsumePower(Long consumePower) {
		this.consumePower = consumePower;
	}

	private Long number;
	
	private Long consumePower;
}
