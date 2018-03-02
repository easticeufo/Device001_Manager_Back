package com.madongfang.api;

import java.util.List;

public class DevicePageApi {

	public Integer getTotalInusePlugNumber() {
		return totalInusePlugNumber;
	}

	public void setTotalInusePlugNumber(Integer totalInusePlugNumber) {
		this.totalInusePlugNumber = totalInusePlugNumber;
	}

	public Integer getTotalInusePlugConsumePower() {
		return totalInusePlugConsumePower;
	}

	public void setTotalInusePlugConsumePower(Integer totalInusePlugConsumePower) {
		this.totalInusePlugConsumePower = totalInusePlugConsumePower;
	}

	public List<DeviceApi> getContent() {
		return content;
	}

	public void setContent(List<DeviceApi> content) {
		this.content = content;
	}

	public Integer getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(Integer totalPages) {
		this.totalPages = totalPages;
	}

	public Long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(Long totalElements) {
		this.totalElements = totalElements;
	}

	public Boolean getLast() {
		return last;
	}

	public void setLast(Boolean last) {
		this.last = last;
	}

	public Integer getNumberOfElements() {
		return numberOfElements;
	}

	public void setNumberOfElements(Integer numberOfElements) {
		this.numberOfElements = numberOfElements;
	}

	public Boolean getFirst() {
		return first;
	}

	public void setFirst(Boolean first) {
		this.first = first;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getAliveDeviceNumber() {
		return aliveDeviceNumber;
	}

	public void setAliveDeviceNumber(Integer aliveDeviceNumber) {
		this.aliveDeviceNumber = aliveDeviceNumber;
	}

	private Integer totalInusePlugNumber; // 表示当前在用插座的总数
	
	private Integer totalInusePlugConsumePower; // 表示当前在用插座已经消耗的总电量，单位为千分之一度
	
	private List<DeviceApi> content;
	
	private Integer totalPages;
	
	private Long totalElements;
	
	private Boolean last;
	
	private Integer numberOfElements;
	
	private Boolean first;
	
	private Integer size;
	
	private Integer number;
	
	private Integer aliveDeviceNumber;
}
