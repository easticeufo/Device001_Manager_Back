package com.madongfang.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.madongfang.api.FinanceApi;
import com.madongfang.repository.CustomRecordRepository;
import com.madongfang.repository.ExceptionCustomRecordRepository;
import com.madongfang.repository.GiftCustomRecordRepository;
import com.madongfang.repository.PayCustomRecordRepository;
import com.madongfang.repository.RefundCustomRecordRepository;
import com.madongfang.repository.SplitRecordRepository;
import com.madongfang.repository.StartCustomRecordRepository;
import com.madongfang.repository.StopCustomRecordRepository;

@Service
public class FinanceService {

	public FinanceApi getFinance(Date startTime, Date stopTime) {
		FinanceApi financeApi = new FinanceApi();
		financeApi.setAlipayIncome(payCustomRecordRepository.getAlipayAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomExceptionAmount(exceptionCustomRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomGiftAmount(giftCustomRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomPayAmount(payCustomRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomRefundAmount(refundCustomRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomStartAmount(startCustomRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomStopAmount(stopCustomRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setCustomTotalAmount(customRecordRepository.getSumAmountByTimeBetween(startTime, stopTime));
		financeApi.setManagerSplits(splitRecordRepository.getManagerSplitsByTimeBetween(startTime, stopTime));
		financeApi.setManualIncome(payCustomRecordRepository.getManualAmountByTimeBetween(startTime, stopTime));
		financeApi.setWechatIncome(payCustomRecordRepository.getWechatAmountByTimeBetween(startTime, stopTime));
		
		return financeApi;
	}
	
	@Autowired
	private PayCustomRecordRepository payCustomRecordRepository;
	
	@Autowired
	private StartCustomRecordRepository startCustomRecordRepository;
	
	@Autowired
	private ExceptionCustomRecordRepository exceptionCustomRecordRepository;
	
	@Autowired
	private GiftCustomRecordRepository giftCustomRecordRepository;
	
	@Autowired
	private RefundCustomRecordRepository refundCustomRecordRepository;
	
	@Autowired
	private StopCustomRecordRepository stopCustomRecordRepository;
	
	@Autowired
	private CustomRecordRepository customRecordRepository;
	
	@Autowired
	private SplitRecordRepository splitRecordRepository;
}
