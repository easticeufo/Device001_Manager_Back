package com.madongfang.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.madongfang.api.DeviceApi;
import com.madongfang.api.DeviceBatchApi;
import com.madongfang.api.DevicePageApi;
import com.madongfang.api.DevicePromptApi;
import com.madongfang.api.PlugApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Area;
import com.madongfang.entity.Device;
import com.madongfang.entity.DevicePrompt;
import com.madongfang.entity.Manager;
import com.madongfang.entity.Plug;
import com.madongfang.entity.PlugPK;
import com.madongfang.exception.HttpBadRequestException;
import com.madongfang.exception.HttpNotAcceptableException;
import com.madongfang.exception.HttpNotFoundException;
import com.madongfang.repository.AreaRepository;
import com.madongfang.repository.DevicePromptRepository;
import com.madongfang.repository.DeviceRepository;
import com.madongfang.repository.InusePlugInfo;
import com.madongfang.repository.ManagerRepository;
import com.madongfang.repository.PlugRepository;
import com.madongfang.repository.StartCustomRecordRepository;

@Service
@Transactional(isolation=Isolation.SERIALIZABLE)
public class DeviceService {

	public DeviceApi addDevice(DeviceApi deviceApi) {
		ReturnApi returnApi = new ReturnApi();
		
		Device device = deviceRepository.findOne(deviceApi.getId());
		if (device != null)
		{
			returnApi.setReturnCode(-1);
			returnApi.setReturnMsg("该设备已经添加");
			throw new HttpBadRequestException(returnApi);
		}
		
		String areaName = "新增设备";
		Area area = areaRepository.findByName(areaName);
		if (area == null)
		{
			area = new Area();
			area.setCardPassword("FFFFFFFFFFFF");
			area.setName(areaName);
			areaRepository.save(area);
		}
		
		device = new Device();
		device.setAliveTime(null);
		device.setArea(area);
		device.setAttachPrice(deviceApi.getAttachPrice());
		device.setCode(deviceApi.getCode());
		device.setFactor(deviceApi.getFactor());
		device.setFloatChargeTime(deviceApi.getFloatChargeTime());
		device.setId(deviceApi.getId());
		device.setMaxDevicePower(deviceApi.getMaxDevicePower());
		device.setMaxPlugPower(deviceApi.getMaxPlugPower());
		device.setMinPrice(deviceApi.getMinPrice());
		device.setName(deviceApi.getLocation());
		device.setPlugNumber(deviceApi.getPlugNumber());
		device.setStatus("N");
		device.setUnitPrice(deviceApi.getUnitPrice());
		deviceRepository.save(device);
		
		for (int i = 1; i <= deviceApi.getPlugNumber(); i++)
		{
			Plug plug = new Plug();
			plug.setConsumePower(0);
			plug.setCustom(null);
			plug.setDeviceCode(device.getCode());
			plug.setId(i);
			plug.setInUse(false);
			plug.setLimitPrice(0);
			plug.setPower(0);
			plug.setStartTime(null);
			plug.setUpdateTime(null);
			plugRepository.save(plug);
		}
		
		return deviceApi;
	}
	
	public List<DeviceApi> getDevices() {
		List<DeviceApi> devices = new LinkedList<DeviceApi>();
		
		for (Device device : deviceRepository.findAll()) {
			DeviceApi deviceApi = convertDevice(device);
			int inusePlugNumber = plugRepository.getInusePlugNumber(device.getCode());
			deviceApi.setInusePlugNumber(inusePlugNumber);
			
			devices.add(deviceApi);
		}
		
		return devices;
	}
	
	public DevicePageApi getDevices(int managerId, Pageable pageable, 
			String deviceCode, List<Integer> areaIds, String location) 
	{
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpNotFoundException(new ReturnApi(-1, "管理员不存在"));
		}
		
		List<Area> areas;
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
		if (deviceCode == null)
		{
			deviceCode = "";
		}
		if (location == null)
		{
			location = "";
		}
		
		Date minTime = new Date();
		minTime.setTime(minTime.getTime() - 60 * 60 * 1000); // 一小时都没有收到设备上报的状态时认为离线
		Page<Device> devices = deviceRepository.findByAreaInAndCodeContainingAndNameContaining(areas, deviceCode, location, pageable);
		int aliveDeviceNumber = deviceRepository.getAliveDeviceNumberInAreas(areas, deviceCode, location, minTime);		
		List<DeviceApi> content = new LinkedList<DeviceApi>();

		for (Device device : devices) {
			DeviceApi deviceApi = convertDevice(device);
			int inusePlugNumber = plugRepository.getInusePlugNumber(device.getCode());
			deviceApi.setInusePlugNumber(inusePlugNumber);
			if (device.getAliveTime() == null 
					|| device.getAliveTime().getTime() <= minTime.getTime()) // 一小时都没有收到设备上报的状态时认为离线
			{
				deviceApi.setIsOnline(false);
			}
			else 
			{
				deviceApi.setIsOnline(true);
			}
			content.add(deviceApi);
		}
		
		DevicePageApi devicePageApi = new DevicePageApi();
		devicePageApi.setContent(content);
		devicePageApi.setFirst(devices.isFirst());
		devicePageApi.setLast(devices.isLast());
		devicePageApi.setNumber(devices.getNumber());
		devicePageApi.setNumberOfElements(devices.getNumberOfElements());
		devicePageApi.setSize(devices.getSize());
		devicePageApi.setTotalElements(devices.getTotalElements());
		InusePlugInfo inusePlugInfo = new InusePlugInfo(0l, 0l);
		if (areas.size() != 0)
		{
			inusePlugInfo = plugRepository.getInusePlugInfo(areas, deviceCode, location);
		}
		if (inusePlugInfo.getConsumePower() != null)
		{
			devicePageApi.setTotalInusePlugConsumePower(inusePlugInfo.getConsumePower().intValue());
		}
		else
		{
			devicePageApi.setTotalInusePlugConsumePower(0);
		}
		devicePageApi.setTotalInusePlugNumber(inusePlugInfo.getNumber().intValue());
		devicePageApi.setTotalPages(devices.getTotalPages());
		devicePageApi.setAliveDeviceNumber(aliveDeviceNumber);
		
		return devicePageApi;
	}
	
	public ReturnApi updateDevices(int managerId, DeviceBatchApi deviceBatchApi) {
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpBadRequestException(new ReturnApi(-1, "管理员不存在"));
		}
		
		List<String> deviceCodes = deviceBatchApi.getDeviceCodes();
		deviceCodes.retainAll(getDeviceCodes(manager));
		if (deviceCodes.size() == 0)
		{
			throw new HttpNotAcceptableException(new ReturnApi(-2, "没有需要修改的设备"));
		}
		
		List<Device> devices = deviceRepository.findByCodeIn(deviceCodes);
		for (Device device : devices) {
			if (deviceBatchApi.getAreaId() != null)
			{
				for (Area area : manager.getAreas()) {
					if (deviceBatchApi.getAreaId() == area.getId())
					{
						device.setArea(area);
						break;
					}
				}
			}
			if (deviceBatchApi.getAttachPrice() != null)
			{
				device.setAttachPrice(deviceBatchApi.getAttachPrice());
			}
			if (deviceBatchApi.getFloatChargeTime() != null)
			{
				device.setFloatChargeTime(deviceBatchApi.getFloatChargeTime());
			}
			if (deviceBatchApi.getMinPrice() != null)
			{
				device.setMinPrice(deviceBatchApi.getMinPrice());
			}
			if (deviceBatchApi.getOverdraft() != null)
			{
				device.setOverdraft(deviceBatchApi.getOverdraft());
			}
			if (deviceBatchApi.getUnitPrice() != null)
			{
				device.setUnitPrice(deviceBatchApi.getUnitPrice());
			}
		}
		
		deviceRepository.save(devices);

		return new ReturnApi(0, "OK");
	}
	
	public DeviceApi getDevice(int managerId, String deviceCode) {
		Device device = deviceRepository.findByCode(deviceCode);
		if (device == null)
		{
			logger.warn("设备不存在:deviceCode={}", deviceCode);
			throw new HttpNotFoundException(new ReturnApi(-1, "设备不存在"));
		}
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpBadRequestException(new ReturnApi(-2, "管理员不存在"));
		}
		if (!deviceBelongToManager(device, manager))
		{
			throw new HttpNotAcceptableException(new ReturnApi(-3, "设备不属于当前管理员"));
		}

		return convertDevice(device);
	}
	
	public DeviceApi updateDevice(int managerId, String deviceCode, DeviceApi deviceApi) {
		Device device = deviceRepository.findByCode(deviceCode);
		if (device == null)
		{
			logger.warn("设备不存在:deviceCode={}", deviceCode);
			throw new HttpNotFoundException(new ReturnApi(-1, "设备不存在"));
		}
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpBadRequestException(new ReturnApi(-2, "管理员不存在"));
		}
		if (!deviceBelongToManager(device, manager))
		{
			throw new HttpNotAcceptableException(new ReturnApi(-3, "设备不属于当前管理员"));
		}
		
		if (deviceApi.getAreaId() != null)
		{
			Area area = areaRepository.findOne(deviceApi.getAreaId());
			if (area == null)
			{
				throw new HttpBadRequestException(new ReturnApi(-4, "设置的小区不存在"));
			}
			
			device.setArea(area);
		}
		if (deviceApi.getAttachPrice() != null)
		{
			device.setAttachPrice(deviceApi.getAttachPrice());
		}
		if (deviceApi.getFactor() != null)
		{
			device.setFactor(deviceApi.getFactor());
		}
		if (deviceApi.getFloatChargeTime() != null)
		{
			device.setFloatChargeTime(deviceApi.getFloatChargeTime());
		}
		if (deviceApi.getLocation() != null)
		{
			device.setName(deviceApi.getLocation());
		}
		if (deviceApi.getMaxDevicePower() != null)
		{
			device.setMaxDevicePower(deviceApi.getMaxDevicePower());
		}
		if (deviceApi.getMaxPlugPower() != null)
		{
			device.setMaxPlugPower(deviceApi.getMaxPlugPower());
		}
		if (deviceApi.getMinPrice() != null)
		{
			device.setMinPrice(deviceApi.getMinPrice());
		}
		if (deviceApi.getOverdraft() != null)
		{
			device.setOverdraft(deviceApi.getOverdraft());
		}
		if (deviceApi.getUnitPrice() != null)
		{
			device.setUnitPrice(deviceApi.getUnitPrice());
		}
		if (deviceApi.getType() != null)
		{
			device.setType(deviceApi.getType());
		}
		
		device = deviceRepository.save(device);

		return convertDevice(device);
	}
	
	public List<DevicePromptApi> getPrompts(String deviceCode)
	{
		List<DevicePromptApi> devicePrompts = new LinkedList<DevicePromptApi>();
		
		for (DevicePrompt devicePrompt : devicePromptRepository.findByDeviceCodeAndTime(deviceCode, new Date())) 
		{
			DevicePromptApi devicePromptApi = new DevicePromptApi();
			devicePromptApi.setContent(devicePrompt.getContent());
			devicePromptApi.setDeviceCode(deviceCode);
			devicePromptApi.setId(devicePrompt.getId());
			devicePromptApi.setStartTime(devicePrompt.getStartTime());
			devicePromptApi.setStopTime(devicePrompt.getStopTime());
			devicePromptApi.setTitle(devicePrompt.getTitle());
			
			devicePrompts.add(devicePromptApi);
		}
		
		return devicePrompts;
	}
	
	public DeviceApi repairDevice(String deviceCode)
	{
		if (deviceCode.matches(".*-.*"))
		{
			throw new HttpBadRequestException(new ReturnApi(-1, "该设备为虚拟设备，无法进行维修"));
		}
		Device device = deviceRepository.findByCode(deviceCode);
		if (device == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-2, "无效的deviceCode"));
		}
		if (plugRepository.getInusePlugNumber(deviceCode) != 0)
		{
			throw new HttpBadRequestException(new ReturnApi(-3, "有插座正在使用，请先停止插座充电"));
		}
		
		/* 新增虚拟设备，仅供未来查询记录使用 */
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date now = new Date();
		List<String> deviceCodes = deviceRepository.getDeviceCodesLike(deviceCode+"-");
		int maxRepairId = 0;
		for (String code : deviceCodes) {
			int repairId = Integer.valueOf(code.substring((deviceCode+"-").length()));
			if (repairId > maxRepairId)
			{
				maxRepairId = repairId;
			}
		}
		Device virtualDevice = new Device();
		virtualDevice.setAliveTime(device.getAliveTime());
		virtualDevice.setArea(device.getArea());
		virtualDevice.setAttachPrice(device.getAttachPrice());
		virtualDevice.setCode(deviceCode+"-"+String.valueOf(maxRepairId+1));
		virtualDevice.setFactor(device.getFactor());
		virtualDevice.setFloatChargeTime(device.getFloatChargeTime());
		virtualDevice.setId(device.getId()+"-"+String.valueOf(maxRepairId+1));
		virtualDevice.setMaxDevicePower(device.getMaxDevicePower());
		virtualDevice.setMaxPlugPower(device.getMaxPlugPower());
		virtualDevice.setMinPrice(device.getMinPrice());
		virtualDevice.setName(device.getName()+"-"+sdf.format(now));
		virtualDevice.setOverdraft(device.getOverdraft());
		virtualDevice.setPlugNumber(device.getPlugNumber());
		virtualDevice.setStatus(device.getStatus());
		virtualDevice.setUnitPrice(device.getUnitPrice());
		deviceRepository.saveAndFlush(virtualDevice);
		for (int i = 1; i <= virtualDevice.getPlugNumber(); i++)
		{
			Plug plug = new Plug();
			plug.setConsumePower(0);
			plug.setCustom(null);
			plug.setDeviceCode(virtualDevice.getCode());
			plug.setId(i);
			plug.setInUse(false);
			plug.setLimitPrice(0);
			plug.setPower(0);
			plug.setStartTime(null);
			plug.setUpdateTime(null);
			plugRepository.save(plug);
		}
		
		String areaName = "维修设备";
		Area area = areaRepository.findByName(areaName);
		if (area == null)
		{
			area = new Area();
			area.setCardPassword("FFFFFFFFFFFF");
			area.setName(areaName);
			areaRepository.save(area);
		}
		
		device.setName(device.getArea().getName() + device.getName() + "-" + sdf.format(now));
		device.setArea(area);
		deviceRepository.save(device);
		
		int count = startCustomRecordRepository.updateDevice(virtualDevice, device);
		logger.debug("update count={}", count);
		
		return convertDevice(virtualDevice);
	}
	
	public PlugApi setPlugStatus(String deviceCode, Integer plugId, String plugStatus)
	{
		Plug plug = plugRepository.findOne(new PlugPK(plugId, deviceCode));
		if (plug == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-1, "插座不存在"));
		}
		
		plug.setStatus(plugStatus);
		
		plugRepository.save(plug);
		
		PlugApi plugApi = new PlugApi();
		plugApi.setId(plugId);
		plugApi.setStatus(plugStatus);
		
		return plugApi;
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private PlugRepository plugRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private ManagerRepository managerRepository;
	
	@Autowired
	private DevicePromptRepository devicePromptRepository;
	
	@Autowired
	private StartCustomRecordRepository startCustomRecordRepository;
	
	private DeviceApi convertDevice(Device device)
	{
		DeviceApi deviceApi = new DeviceApi();
		if (device.getArea() != null)
		{
			deviceApi.setArea(device.getArea().getName());
			deviceApi.setAreaId(device.getArea().getId());
		}
		deviceApi.setAttachPrice(device.getAttachPrice());
		deviceApi.setCode(device.getCode());
		deviceApi.setFactor(device.getFactor());
		deviceApi.setFloatChargeTime(device.getFloatChargeTime());
		deviceApi.setId(device.getId());
		deviceApi.setLocation(device.getName());
		deviceApi.setMaxDevicePower(device.getMaxDevicePower());
		deviceApi.setMaxPlugPower(device.getMaxPlugPower());
		deviceApi.setMinPrice(device.getMinPrice());
		deviceApi.setOverdraft(device.getOverdraft());
		deviceApi.setPlugNumber(device.getPlugNumber());
		deviceApi.setUnitPrice(device.getUnitPrice());
		deviceApi.setType(device.getType());
		
		return deviceApi;
	}
	
	private boolean deviceBelongToManager(Device device, Manager manager)
	{
		if (device.getArea() == null || manager.getAreas() == null)
		{
			logger.warn("设备小区或管理员小区为null");
			return false;
		}

		for (Area area : manager.getAreas()) {
			if (area.getId() == device.getArea().getId())
			{
				return true;
			}
		}
		
		return false;
	}
	
	private List<String> getDeviceCodes(Manager manager)
	{
		if (manager == null || manager.getAreas() == null || manager.getAreas().size() == 0)
		{
			return new LinkedList<String>();
		}
		
		return deviceRepository.getDeviceCodes(manager.getAreas());
	}
}
