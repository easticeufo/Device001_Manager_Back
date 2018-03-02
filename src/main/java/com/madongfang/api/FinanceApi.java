package com.madongfang.api;

import java.util.List;

public class FinanceApi {

	public Integer getCustomStartAmount() {
		return customStartAmount;
	}

	public void setCustomStartAmount(Integer customStartAmount) {
		this.customStartAmount = customStartAmount;
	}

	public Integer getCustomStopAmount() {
		return customStopAmount;
	}

	public void setCustomStopAmount(Integer customStopAmount) {
		this.customStopAmount = customStopAmount;
	}

	public Integer getCustomExceptionAmount() {
		return customExceptionAmount;
	}

	public void setCustomExceptionAmount(Integer customExceptionAmount) {
		this.customExceptionAmount = customExceptionAmount;
	}

	public Integer getCustomRefundAmount() {
		return customRefundAmount;
	}

	public void setCustomRefundAmount(Integer customRefundAmount) {
		this.customRefundAmount = customRefundAmount;
	}

	public Integer getCustomPayAmount() {
		return customPayAmount;
	}

	public void setCustomPayAmount(Integer customPayAmount) {
		this.customPayAmount = customPayAmount;
	}

	public Integer getCustomGiftAmount() {
		return customGiftAmount;
	}

	public void setCustomGiftAmount(Integer customGiftAmount) {
		this.customGiftAmount = customGiftAmount;
	}

	public Integer getCustomTotalAmount() {
		return customTotalAmount;
	}

	public void setCustomTotalAmount(Integer customTotalAmount) {
		this.customTotalAmount = customTotalAmount;
	}

	public Integer getWechatIncome() {
		return wechatIncome;
	}

	public void setWechatIncome(Integer wechatIncome) {
		this.wechatIncome = wechatIncome;
	}

	public Integer getAlipayIncome() {
		return alipayIncome;
	}

	public void setAlipayIncome(Integer alipayIncome) {
		this.alipayIncome = alipayIncome;
	}

	public Integer getManualIncome() {
		return manualIncome;
	}

	public void setManualIncome(Integer manualIncome) {
		this.manualIncome = manualIncome;
	}

	public List<ManagerSplitApi> getManagerSplits() {
		return managerSplits;
	}

	public void setManagerSplits(List<ManagerSplitApi> managerSplits) {
		this.managerSplits = managerSplits;
	}
	
	private Integer customStartAmount;
	
	private Integer customStopAmount;
	
	private Integer customExceptionAmount;
	
	private Integer customRefundAmount;
	
	private Integer customPayAmount;
	
	private Integer customGiftAmount;
	
	private Integer customTotalAmount;
	
	private Integer wechatIncome;
	
	private Integer alipayIncome;
	
	private Integer manualIncome;
	
	private List<ManagerSplitApi> managerSplits;
}
