package com.madongfang.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.madongfang.api.AreaApi;
import com.madongfang.api.ChargeRecordApi;
import com.madongfang.api.DeviceApi;
import com.madongfang.api.DeviceBatchApi;
import com.madongfang.api.DevicePageApi;
import com.madongfang.api.ManagerApi;
import com.madongfang.api.PlugApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.data.ChargeRecordExcel;
import com.madongfang.entity.Manager;
import com.madongfang.exception.HttpInternalServerErrorException;
import com.madongfang.exception.HttpNotAcceptableException;
import com.madongfang.service.CustomRecordService;
import com.madongfang.service.DeviceService;
import com.madongfang.service.InviteCodeService;
import com.madongfang.service.ManagerService;
import com.madongfang.util.ExcelUtil;

@RestController
@RequestMapping(value="/api/mine")
public class MineController {
	
	@GetMapping(value="/info")
	public ManagerApi getInfo(@SessionAttribute Manager manager)
	{
		return managerService.getManager(manager.getId());
	}
	
	@GetMapping(value="/areas")
	public List<AreaApi> getAreas(@SessionAttribute Manager manager)
	{
		return managerService.getAreas(manager.getId());
	}
	
	@GetMapping(value="/areas/{areaId}/devices")
	public List<DeviceApi> getAreaDevices(@SessionAttribute Manager manager, @PathVariable Integer areaId)
	{
		return managerService.getAreaDevices(manager.getId(), areaId);
	}
	
	@GetMapping(value="/devices", params="page")
	public DevicePageApi getDevices(@SessionAttribute Manager manager, 
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) List<Integer> areaIds,
			@RequestParam(required=false) String location,
			@PageableDefault(sort={"code"}, direction=Direction.ASC) Pageable pageable) 
	{
		logger.debug("getDevices:deviceCode={}, areaIds={}, location={}", deviceCode, areaIds, location);
		return deviceService.getDevices(manager.getId(), pageable, deviceCode, areaIds, location);
	}
	
	@PutMapping(value="/devices")
	public ReturnApi updateDevices(@SessionAttribute Manager manager, @RequestBody DeviceBatchApi deviceBatchApi)
	{
		try {
			return deviceService.updateDevices(manager.getId(), deviceBatchApi);
		} catch (DataIntegrityViolationException e) {
			// TODO: handle exception
			throw new HttpNotAcceptableException(new ReturnApi(-3, "数据库修改失败！"));
		}
	}
	
	@GetMapping(value="/devices/{deviceCode}")
	public DeviceApi getDevice(@SessionAttribute Manager manager, @PathVariable String deviceCode)
	{
		return deviceService.getDevice(manager.getId(), deviceCode);
	}
	
	@PutMapping(value="/devices/{deviceCode}")
	public DeviceApi updateDevice(@SessionAttribute Manager manager, @PathVariable String deviceCode, 
			@RequestBody DeviceApi deviceApi)
	{
		return deviceService.updateDevice(manager.getId(), deviceCode, deviceApi);
	}
	
	@GetMapping(value="/devices/{deviceCode}/plugs")
	public List<PlugApi> getDevicePlugs(@SessionAttribute Manager manager, @PathVariable String deviceCode)
	{
		return managerService.getDevicePlugs(manager.getId(), deviceCode);
	}
	
	@GetMapping(value="/records/charge", params="page")
	public Page<ChargeRecordApi> getChargeRecords(
			@SessionAttribute Manager manager, 
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) List<Integer> areaIds,
			@RequestParam(required=false) String location,
			@RequestParam(required=false) Long startTime,
			@RequestParam(required=false) Long stopTime,
			@PageableDefault(size=30) Pageable pageable) 
	{
		logger.debug("getChargeRecords:deviceCode={}, areaIds={}, location={}, startTime={}, stopTime={}", 
				deviceCode, areaIds, location, startTime, stopTime);
		return customRecordService.getChargeRecords(manager.getId(), deviceCode, areaIds, location, startTime, stopTime, pageable);
	}
	
	@GetMapping(value="/records/charge/excel")
	public String exportChargeRecordExcel(HttpServletResponse response, 
			@SessionAttribute Manager manager, 
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) List<Integer> areaIds,
			@RequestParam(required=false) String location,
			@RequestParam(required=false) Long startTime,
			@RequestParam(required=false) Long stopTime)
	{
		try {
			List<ChargeRecordExcel> chargeRecordExcel = customRecordService.getChargeRecords(manager.getId(), deviceCode, areaIds, location, startTime, stopTime);
			
			response.setContentType("application/binary;charset=ISO8859_1");
			String filename = new String("充电记录.xlsx".getBytes(), "ISO8859_1");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			String[] titles = {"设备编号", "设备区域", "设备位置", "插座位置", "开始充电时间", "结束充电时间", "持续时间", "消耗电量(度)", "充电金额(元)", "用户ID", "用户昵称"};
			excelUtil.export(response.getOutputStream(), titles, chargeRecordExcel);
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("导出excel异常:", e);
			throw new HttpInternalServerErrorException(new ReturnApi(-1, "导出excel失败"));
		}
		
		return null;
	}
	
	@GetMapping(value="/inviteCodes")
	public List<String> getInviteCodes(@SessionAttribute Manager manager, @RequestParam int level, @RequestParam int number) {
		if (manager.getLevel() > 1)
		{
			throw new HttpNotAcceptableException(new ReturnApi(-1, "您不是1级管理员，无权限获取邀请码！"));
		}
		
		return inviteCodeService.getInviteCodes(level, number);
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private ManagerService managerService;
	
	@Autowired
	private DeviceService deviceService;
	
	@Autowired
	private CustomRecordService customRecordService;
	
	@Autowired
	private InviteCodeService inviteCodeService;
	
	@Autowired
	private ExcelUtil excelUtil;
}
