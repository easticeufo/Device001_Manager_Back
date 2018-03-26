package com.madongfang.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.DiscriminatorValue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.madongfang.api.BillRecordApi;
import com.madongfang.api.ChargeRecordApi;
import com.madongfang.api.PaymentRecordApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.data.ChargeRecordExcel;
import com.madongfang.entity.Area;
import com.madongfang.entity.CustomRecord;
import com.madongfang.entity.Device;
import com.madongfang.entity.Manager;
import com.madongfang.entity.PayCustomRecord;
import com.madongfang.entity.StartCustomRecord;
import com.madongfang.entity.StopCustomRecord;
import com.madongfang.exception.HttpBadRequestException;
import com.madongfang.repository.AreaRepository;
import com.madongfang.repository.ChargeRecord;
import com.madongfang.repository.CustomRecordRepository;
import com.madongfang.repository.ManagerRepository;
import com.madongfang.repository.PayCustomRecordRepository;
import com.madongfang.repository.StopCustomRecordRepository;

@Service
public class CustomRecordService {

	public Page<ChargeRecordApi> getChargeRecords(String deviceCode, List<Integer> areaIds, 
			String location, Long startTimeMs, Long stopTimeMs, Pageable pageable) 
	{
		List<Area> areas = null;
		Date startTime;
		Date stopTime;
		
		if (deviceCode == null || deviceCode.length() == 0)
		{
			deviceCode = "%";
		}
		else
		{
			deviceCode = "%" + deviceCode + "%";
		}
		if (location == null || location.length() == 0)
		{
			location = "%";
		}
		else
		{
			location = "%" + location + "%";
		}
		if (areaIds == null)
		{
			areas = areaRepository.findAll();
		}
		else
		{
			areas = areaRepository.findByIdIn(areaIds);
		}
		if (startTimeMs == null)
		{
			startTime = new Date(0);
		}
		else
		{
			startTime = new Date(startTimeMs);
		}
		if (stopTimeMs == null)
		{
			stopTime = new Date();
		}
		else
		{
			stopTime = new Date(stopTimeMs);
		}
		
		Page<ChargeRecord> chargeRecords = customRecordRepository.findChargeRecords(deviceCode, areas, location, startTime, stopTime, pageable);
		return chargeRecords.map(new Converter<ChargeRecord, ChargeRecordApi>() {

			@Override
			public ChargeRecordApi convert(ChargeRecord chargeRecord) {
				// TODO Auto-generated method stub
				ChargeRecordApi chargeRecordApi = new ChargeRecordApi();
				chargeRecordApi.setAmount(-(chargeRecord.getStartAmount() + chargeRecord.getStopAmount()));
				chargeRecordApi.setCustomId(chargeRecord.getCustomId());
				chargeRecordApi.setDeviceArea(chargeRecord.getDeviceArea());
				chargeRecordApi.setDeviceCode(chargeRecord.getDeviceCode());
				chargeRecordApi.setDeviceLocation(chargeRecord.getDeviceLocation());
				chargeRecordApi.setNickname(chargeRecord.getNickname());
				chargeRecordApi.setPlugId(chargeRecord.getPlugId());
				chargeRecordApi.setPowerConsumption(chargeRecord.getPowerConsumption());
				chargeRecordApi.setStartTime(chargeRecord.getStartTime());
				chargeRecordApi.setStopTime(chargeRecord.getStopTime());
				
				return chargeRecordApi;
			}
		});
	}
	
	public Page<ChargeRecordApi> getChargeRecords(int managerId, String deviceCode, List<Integer> areaIds, 
			String location, Long startTimeMs, Long stopTimeMs, Pageable pageable) 
	{
		List<Area> areas = null;
		Date startTime;
		Date stopTime;
		
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpBadRequestException(new ReturnApi(-1, "管理员不存在"));
		}

		if (areaIds == null)
		{
			areas = manager.getAreas();
		}
		else 
		{
			areas = new LinkedList<Area>();
			for (int areaId : areaIds) {
				for (Area area : manager.getAreas()) {
					if (areaId == area.getId())
					{
						areas.add(area);
						break;
					}
				}
			}
		}
		logger.debug("search areas={}", areas);
		if (areas.size() == 0)
		{
			logger.warn("搜索的区域为空");
			throw new HttpBadRequestException(new ReturnApi(-2, "搜索的区域为空"));
		}
		
		if (deviceCode == null || deviceCode.length() == 0)
		{
			deviceCode = "%";
		}
		else
		{
			deviceCode = "%" + deviceCode + "%";
		}
		if (location == null || location.length() == 0)
		{
			location = "%";
		}
		else
		{
			location = "%" + location + "%";
		}
		if (startTimeMs == null)
		{
			startTime = new Date(0);
		}
		else
		{
			startTime = new Date(startTimeMs);
		}
		if (stopTimeMs == null)
		{
			stopTime = new Date();
		}
		else
		{
			stopTime = new Date(stopTimeMs);
		}
		
		Page<ChargeRecord> chargeRecords = customRecordRepository.findChargeRecords(deviceCode, areas, location, startTime, stopTime, pageable);
		return chargeRecords.map(new Converter<ChargeRecord, ChargeRecordApi>() {

			@Override
			public ChargeRecordApi convert(ChargeRecord chargeRecord) {
				// TODO Auto-generated method stub
				ChargeRecordApi chargeRecordApi = new ChargeRecordApi();
				chargeRecordApi.setAmount(-(chargeRecord.getStartAmount() + chargeRecord.getStopAmount()));
				chargeRecordApi.setCustomId(chargeRecord.getCustomId());
				chargeRecordApi.setDeviceArea(chargeRecord.getDeviceArea());
				chargeRecordApi.setDeviceCode(chargeRecord.getDeviceCode());
				chargeRecordApi.setDeviceLocation(chargeRecord.getDeviceLocation());
				chargeRecordApi.setNickname(chargeRecord.getNickname());
				chargeRecordApi.setPlugId(chargeRecord.getPlugId());
				chargeRecordApi.setPowerConsumption(chargeRecord.getPowerConsumption());
				chargeRecordApi.setStartTime(chargeRecord.getStartTime());
				chargeRecordApi.setStopTime(chargeRecord.getStopTime());
				
				return chargeRecordApi;
			}
		});
	}
	
	public Page<BillRecordApi> getBillRecords(int customId, Pageable pageable) {
		Page<CustomRecord> billRecords = customRecordRepository.findByCustomId(customId, pageable);
		
		return billRecords.map(new Converter<CustomRecord, BillRecordApi>() {

			@Override
			public BillRecordApi convert(CustomRecord customRecord) {
				// TODO Auto-generated method stub
				BillRecordApi billRecordApi = new BillRecordApi();
				
				billRecordApi.setAmount(customRecord.getAmount());
				billRecordApi.setBalance(customRecord.getBalance());
				billRecordApi.setTime(customRecord.getTime());
				String type = customRecord.getClass().getAnnotation(DiscriminatorValue.class).value();
				billRecordApi.setType(type);
				
				return billRecordApi;
			}
		});
	}
	
	public Page<PaymentRecordApi> getPaymentRecords(int customId, Pageable pageable) {
		Page<PayCustomRecord> paymentRecords = payCustomRecordRepository.findByCustomId(customId, pageable);
		
		return paymentRecords.map(new Converter<PayCustomRecord, PaymentRecordApi>() {

			@Override
			public PaymentRecordApi convert(PayCustomRecord payCustomRecord) {
				// TODO Auto-generated method stub
				PaymentRecordApi paymentRecordApi = new PaymentRecordApi();
				
				paymentRecordApi.setAmount(payCustomRecord.getAmount());
				paymentRecordApi.setBalance(payCustomRecord.getBalance());
				paymentRecordApi.setTime(payCustomRecord.getTime());
				
				return paymentRecordApi;
			}
		});
	}
	
	public Page<ChargeRecordApi> getChargeRecords(int customId, Pageable pageable) {
		Page<StopCustomRecord> chargeRecords = stopCustomRecordRepository.findByCustomId(customId, pageable);
		
		return chargeRecords.map(new Converter<StopCustomRecord, ChargeRecordApi>() {

			@Override
			public ChargeRecordApi convert(StopCustomRecord stopCustomRecord) {
				// TODO Auto-generated method stub
				StartCustomRecord startCustomRecord = stopCustomRecord.getStartCustomRecord();
				if (startCustomRecord == null)
				{
					logger.error("充电结束记录无对应的充电开始记录！");
					return null;
				}
				
				ChargeRecordApi chargeRecordApi = new ChargeRecordApi();
				Device device = startCustomRecord.getDevice();
				if (device != null)
				{
					if (device.getArea() != null)
					{
						chargeRecordApi.setDeviceArea(device.getArea().getName());
					}
					chargeRecordApi.setDeviceCode(device.getCode());
					chargeRecordApi.setDeviceLocation(device.getName());
				}
				
				chargeRecordApi.setAmount(-startCustomRecord.getAmount() - stopCustomRecord.getAmount());
				chargeRecordApi.setPlugId(startCustomRecord.getPlugId());
				chargeRecordApi.setStartTime(startCustomRecord.getTime());
				chargeRecordApi.setStopTime(stopCustomRecord.getTime());
				
				return chargeRecordApi;
			}
		});
	}
	
	public List<ChargeRecordExcel> getChargeRecords(int managerId, String deviceCode, List<Integer> areaIds, 
			String location, Long startTimeMs, Long stopTimeMs) 
	{
		List<Area> areas = null;
		Date startTime;
		Date stopTime;
		
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpBadRequestException(new ReturnApi(-1, "管理员不存在"));
		}

		if (areaIds == null)
		{
			areas = manager.getAreas();
		}
		else 
		{
			areas = new LinkedList<Area>();
			for (int areaId : areaIds) {
				for (Area area : manager.getAreas()) {
					if (areaId == area.getId())
					{
						areas.add(area);
						break;
					}
				}
			}
		}
		logger.debug("search areas={}", areas);
		if (areas.size() == 0)
		{
			logger.warn("搜索的区域为空");
			throw new HttpBadRequestException(new ReturnApi(-2, "搜索的区域为空"));
		}
		
		if (deviceCode == null || deviceCode.length() == 0)
		{
			deviceCode = "%";
		}
		else
		{
			deviceCode = "%" + deviceCode + "%";
		}
		if (location == null || location.length() == 0)
		{
			location = "%";
		}
		else
		{
			location = "%" + location + "%";
		}
		if (startTimeMs == null)
		{
			startTime = new Date(0);
		}
		else
		{
			startTime = new Date(startTimeMs);
		}
		if (stopTimeMs == null)
		{
			stopTime = new Date();
		}
		else
		{
			stopTime = new Date(stopTimeMs);
		}
		
		List<ChargeRecord> chargeRecords = customRecordRepository.findChargeRecords(deviceCode, areas, location, startTime, stopTime);
		List<ChargeRecordExcel> chargeRecordExcel = new LinkedList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (ChargeRecord chargeRecord : chargeRecords) {
			ChargeRecordExcel chargeRecordExcelUnit = new ChargeRecordExcel();
			chargeRecordExcelUnit.setAmount((float)(-chargeRecord.getStartAmount()-chargeRecord.getStopAmount())/100);
			chargeRecordExcelUnit.setCustomId(chargeRecord.getCustomId());
			chargeRecordExcelUnit.setDeviceArea(chargeRecord.getDeviceArea());
			chargeRecordExcelUnit.setDeviceCode(chargeRecord.getDeviceCode());
			chargeRecordExcelUnit.setDeviceLocation(chargeRecord.getDeviceLocation());
			chargeRecordExcelUnit.setDuration((int)((stopTime.getTime() - startTime.getTime()) / (1000 * 60)));
			chargeRecordExcelUnit.setNickname(chargeRecord.getNickname());
			chargeRecordExcelUnit.setPlugId(chargeRecord.getPlugId());
			chargeRecordExcelUnit.setPowerConsumption((float)chargeRecord.getPowerConsumption()/1000);
			chargeRecordExcelUnit.setStartTime(sdf.format(chargeRecord.getStartTime()));
			chargeRecordExcelUnit.setStopTime(sdf.format(chargeRecord.getStopTime()));
			chargeRecordExcel.add(chargeRecordExcelUnit);
		}
		
		return chargeRecordExcel;
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CustomRecordRepository customRecordRepository;
	
	@Autowired
	private PayCustomRecordRepository payCustomRecordRepository;
	
	@Autowired
	private StopCustomRecordRepository stopCustomRecordRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private ManagerRepository managerRepository;
}
