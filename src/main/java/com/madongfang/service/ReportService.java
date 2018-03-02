package com.madongfang.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.madongfang.api.ReportApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Area;
import com.madongfang.entity.Device;
import com.madongfang.entity.Manager;
import com.madongfang.exception.HttpNotAcceptableException;
import com.madongfang.exception.HttpNotFoundException;
import com.madongfang.repository.CustomRecordRepository;
import com.madongfang.repository.DeviceRepository;
import com.madongfang.repository.ManagerRepository;
import com.madongfang.repository.ReportRecord;

@Service
public class ReportService {

	public List<ReportApi> getAnnualReport(int managerId, int year, 
			Integer areaId, String deviceCode, Integer plugId) 
	{
		List<ReportApi> annualReport = new ArrayList<ReportApi>(13);
		Date now = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, 0, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startTime = calendar.getTime();
		calendar.set(Calendar.YEAR, year + 1);
		Date stopTime = new Date(calendar.getTimeInMillis() - 1);
		if (startTime.getTime() > now.getTime()) // 开始时间大于当前时间则返回空报表
		{
			return annualReport;
		}
		ReportApi reportApi = new ReportApi();
		reportApi.setType("year");
		reportApi.setStartTime(startTime);
		reportApi.setStopTime(stopTime);
		reportApi.setChargeNumber(0);
		reportApi.setDurationTime(0);
		reportApi.setIncome(0);
		reportApi.setPowerConsumption(0);
		annualReport.add(reportApi);
		int month = 11;
		while (month >= 0)
		{
			calendar.set(year, month, 1, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			startTime = calendar.getTime();
			calendar.set(Calendar.MONTH, month + 1);
			stopTime = new Date(calendar.getTimeInMillis() - 1);
			
			if (startTime.getTime() <= now.getTime())
			{
				reportApi = new ReportApi();
				reportApi.setType("month");
				reportApi.setStartTime(startTime);
				reportApi.setStopTime(stopTime);
				reportApi.setChargeNumber(0);
				reportApi.setDurationTime(0);
				reportApi.setIncome(0);
				reportApi.setPowerConsumption(0);
				annualReport.add(reportApi);
			}
			
			month--;
		}
		
		return getReport(managerId, areaId, deviceCode, plugId, annualReport);
	}
	
	public List<ReportApi> getMonthlyReport(int managerId, int year, int month, 
			Integer areaId, String deviceCode, Integer plugId) 
	{
		month--; // 0表示1月
		List<ReportApi> monthlyReport = new ArrayList<ReportApi>(32);
		Date now = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startTime = calendar.getTime();
		calendar.set(Calendar.MONTH, month + 1);
		Date stopTime = new Date(calendar.getTimeInMillis() - 1);
		if (startTime.getTime() > now.getTime()) // 开始时间大于当前时间则返回空报表
		{
			return monthlyReport;
		}
		ReportApi reportApi = new ReportApi();
		reportApi.setType("month");
		reportApi.setStartTime(startTime);
		reportApi.setStopTime(stopTime);
		reportApi.setChargeNumber(0);
		reportApi.setDurationTime(0);
		reportApi.setIncome(0);
		reportApi.setPowerConsumption(0);
		monthlyReport.add(reportApi);
		
		calendar.add(Calendar.MILLISECOND, -1);
		int date = calendar.get(Calendar.DAY_OF_MONTH);
		while (date > 0)
		{
			calendar.set(year, month, date, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			startTime = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH, date + 1);
			stopTime = new Date(calendar.getTimeInMillis() - 1);
			
			if (startTime.getTime() <= now.getTime())
			{
				reportApi = new ReportApi();
				reportApi.setType("date");
				reportApi.setStartTime(startTime);
				reportApi.setStopTime(stopTime);
				reportApi.setChargeNumber(0);
				reportApi.setDurationTime(0);
				reportApi.setIncome(0);
				reportApi.setPowerConsumption(0);
				monthlyReport.add(reportApi);
			}
			
			date--;
		}
		
		return getReport(managerId, areaId, deviceCode, plugId, monthlyReport);
	}
	
	@Autowired
	private ManagerRepository managerRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private CustomRecordRepository customRecordRepository;
	
	private List<ReportApi> getReport(int managerId, Integer areaId, String deviceCode, 
			Integer plugId, List<ReportApi> report) {
		
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "不存在的管理员"));
		}
		List<Area> areas = manager.getAreas();
		List<Device> devices = deviceRepository.findByAreaIn(areas);
		List<ReportRecord> reportRecords;
		for (ReportApi reportApi : report) 
		{
			Date startTime = reportApi.getStartTime();
			Date stopTime = reportApi.getStopTime();
			
			if (areaId == null) // 所有区域
			{
				if (devices.size() == 0)
				{
					break;
				}
				reportRecords = customRecordRepository.findReportByTimeBetweenAndDeviceIn(startTime, stopTime, devices);
			}
			else
			{
				boolean validArea = false;
				for (Area area : areas) {
					if (areaId == area.getId())
					{
						validArea = true;
						break;
					}
				}
				if (!validArea)
				{
					throw new HttpNotAcceptableException(new ReturnApi(-2, "该小区不属于当前管理员"));
				}
				
				if (deviceCode == null) // 某一区域中的所有设备
				{
					devices = deviceRepository.findByAreaId(areaId);
					if (devices.size() == 0)
					{
						break;
					}
					reportRecords = customRecordRepository.findReportByTimeBetweenAndDeviceIn(startTime, stopTime, devices);
				}
				else 
				{
					Device device = deviceRepository.findByCode(deviceCode);
					if (device == null || device.getArea() == null || device.getArea().getId() != areaId)
					{
						throw new HttpNotAcceptableException(new ReturnApi(-3, "该设备不属于该小区"));
					}
					
					if (plugId == null) // 某一区域某一设备的所有插座
					{
						reportRecords = customRecordRepository.findReportByTimeBetweenAndDeviceCode(startTime, stopTime, deviceCode);
					}
					else // 某一区域某一设备的某一插座
					{
						if (plugId > device.getPlugNumber())
						{
							throw new HttpNotAcceptableException(new ReturnApi(-4, "插座编号超出了设备最大插座数"));
						}
						
						reportRecords = customRecordRepository.findReportByTimeBetweenAndDeviceCodeAndPlugId(startTime, stopTime, deviceCode, plugId);
					}
				}
			}
			
			int chargeNumber = 0;
			int durationTime = 0;
			int income = 0;
			int powerConsumption = 0;
			for (ReportRecord reportRecord : reportRecords) {
				if (-reportRecord.getStartAmount() != reportRecord.getStopAmount() || reportRecord.getPowerConsumption() != 0) // 仅统计有效充电记录
				{
					chargeNumber++;
					durationTime += (reportRecord.getStopTime().getTime() - reportRecord.getStartTime().getTime()) / 1000;
					income += -(reportRecord.getStartAmount() + reportRecord.getStopAmount());
					powerConsumption += reportRecord.getPowerConsumption();
				}
			}
			reportApi.setChargeNumber(chargeNumber);
			reportApi.setDurationTime(durationTime);
			reportApi.setIncome(income);
			reportApi.setPowerConsumption(powerConsumption);
		}

		return report;
	}
}
