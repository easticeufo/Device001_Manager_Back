package com.madongfang.api;

import java.util.Date;

public class PlugApi {

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getLimitPrice() {
		return limitPrice;
	}

	public void setLimitPrice(Integer limitPrice) {
		this.limitPrice = limitPrice;
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

	public Integer getPower() {
		return power;
	}

	public void setPower(Integer power) {
		this.power = power;
	}

	public Integer getConsumePower() {
		return consumePower;
	}

	public void setConsumePower(Integer consumePower) {
		this.consumePower = consumePower;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	private Integer id;
	
	private String status;
	
	private Integer limitPrice;

	private Integer customId;
	
	private String nickname;
	
	private Integer power;
	
	private Integer consumePower; // 表示插座当前充电已经消耗的电量，单位为千分之一度
	
	private Date startTime;
	
	private Date updateTime;
}
