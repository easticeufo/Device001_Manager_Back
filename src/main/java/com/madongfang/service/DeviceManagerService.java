package com.madongfang.service;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.madongfang.data.ManagerSplit;
import com.madongfang.entity.DeviceManager;
import com.madongfang.entity.Manager;
import com.madongfang.entity.SplitRecord;
import com.madongfang.repository.CustomRecordRepository;
import com.madongfang.repository.DeviceIncome;
import com.madongfang.repository.DeviceManagerRepository;
import com.madongfang.repository.ManagerRepository;
import com.madongfang.repository.SplitRecordRepository;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.WechatUtil;

@Service
public class DeviceManagerService {

	public void splitMoney() {
		List<String> deviceCodes = deviceManagerRepository.getSplitDeviceCodes();
		if (deviceCodes.size() == 0)
		{
			logger.info("没有需要分账的设备");
			return;
		}
		
		/* 进行分账的是前一天的设备收入 */
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startTime = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		Date stopTime = calendar.getTime();
		
		List<DeviceIncome> deviceIncomeList = customRecordRepository.getDeviceIncome(deviceCodes, startTime, stopTime);
		logger.debug("设备收入列表:{}", deviceIncomeList);
		List<DeviceManager> deviceManagers = deviceManagerRepository.findByDeviceCodeIn(deviceCodes);
		
		List<ManagerSplit> managerSplits = new LinkedList<ManagerSplit>();
		for (DeviceManager deviceManager : deviceManagers) {
			ManagerSplit managerSplit = getManagerSplit(managerSplits, deviceManager.getManagerId());
			if (managerSplit == null)
			{
				continue;
			}
			
			int amount = getDeviceIncome(deviceIncomeList, deviceManager.getDeviceCode()) * deviceManager.getSplitPercent() / 100;
			managerSplit.setSplitAmount(managerSplit.getSplitAmount() + amount);
		}
		
		for (ManagerSplit managerSplit : managerSplits) {
			if (managerSplit.getSplitAmount() <= 0)
			{
				continue;
			}
			logger.info("管理员(id={})分账金额为{}分", managerSplit.getManagerId(), managerSplit.getSplitAmount());
			String transferNumber = "M" + managerSplit.getManagerId() + "A" + managerSplit.getSplitAmount() + "T" + System.currentTimeMillis() + "R";
			transferNumber += commonUtil.getRandomStringByLength(32 - transferNumber.length());
			boolean splitSuccess = true;
			try {
				if (!wechatUtil.transfer(transferNumber, managerSplit.getSplitAmount(), managerSplit.getOpenId(), "云支付供电站收入"))
				{
					splitSuccess = false;
				}
			} catch (KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
					| CertificateException | IOException e) {
				// TODO Auto-generated catch block
				splitSuccess = false;
				logger.error("catch Exception:", e);
			}
			if (!splitSuccess)
			{
				logger.error("管理员(id={})分账失败!", managerSplit.getManagerId());
			}
			
			SplitRecord splitRecord = new SplitRecord();
			splitRecord.setAmount(managerSplit.getSplitAmount());
			splitRecord.setIsSuccess(splitSuccess);
			splitRecord.setManagerId(managerSplit.getManagerId());
			splitRecord.setTime(now);
			splitRecord.setTransferNumber(transferNumber);
			splitRecordRepository.save(splitRecord);
		}
		
		logger.info("分账完成");
	}
	
	public void checkSplitMoney() {
		Date now = new Date();
		List<SplitRecord> splitRecords = splitRecordRepository.findByIsSuccessFalseAndTimeAfter(new Date(now.getTime() - 3 * 24 * 60 * 60 * 1000)); // 查找3天之内的分账失败的记录
		for (SplitRecord splitRecord : splitRecords) {
			Manager manager = managerRepository.findOne(splitRecord.getManagerId());
			if (manager == null)
			{
				logger.error("管理员不存在,managerId={}", splitRecord.getManagerId());
				continue;
			}
			logger.info("管理员(id={})补分账金额为{}分", splitRecord.getManagerId(), splitRecord.getAmount());
			try {
				if (wechatUtil.transfer(splitRecord.getTransferNumber(), splitRecord.getAmount(), manager.getOpenId(), "云支付供电站收入"))
				{
					splitRecord.setIsSuccess(true);
					splitRecord.setTime(now);
					splitRecordRepository.save(splitRecord);
				}
				else
				{
					logger.error("管理员(id={})分账失败!", splitRecord.getManagerId());
				}
			} catch (KeyManagementException | UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException
					| CertificateException | IOException e) {
				// TODO Auto-generated catch block
				logger.error("catch Exception:", e);
			}
		}
		
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DeviceManagerRepository deviceManagerRepository;
	
	@Autowired
	private CustomRecordRepository customRecordRepository;
	
	@Autowired
	private ManagerRepository managerRepository;
	
	@Autowired
	private SplitRecordRepository splitRecordRepository;
	
	@Autowired
	private WechatUtil wechatUtil;
	
	@Autowired
	private CommonUtil commonUtil;
	
	private ManagerSplit getManagerSplit(List<ManagerSplit> managerSplits, Integer managerId)
	{
		for (int i = 0; i < managerSplits.size(); i++) {
			if (managerSplits.get(i).getManagerId() == managerId)
			{
				return managerSplits.get(i);
			}
		}
		
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.error("所要分账的管理员不存在:managerId={}", managerId);
			return null;
		}
		ManagerSplit managerSplit = new ManagerSplit(managerId, manager.getOpenId());
		managerSplits.add(managerSplit);
		return managerSplit;
	}
	
	private int getDeviceIncome(List<DeviceIncome> deviceIncomeList, String deviceCode)
	{
		if (deviceCode == null)
		{
			return 0;
		}
		
		for (DeviceIncome deviceIncome : deviceIncomeList) {
			if (deviceCode.equals(deviceIncome.getDeviceCode()))
			{
				return deviceIncome.getIncome().intValue();
			}
		}
		
		return 0;
	}
}
