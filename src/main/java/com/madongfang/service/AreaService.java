package com.madongfang.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.madongfang.api.AreaApi;
import com.madongfang.api.DeviceApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Area;
import com.madongfang.entity.Device;
import com.madongfang.entity.Manager;
import com.madongfang.exception.HttpBadRequestException;
import com.madongfang.exception.HttpNotAcceptableException;
import com.madongfang.exception.HttpNotFoundException;
import com.madongfang.repository.AreaRepository;
import com.madongfang.repository.DeviceRepository;
import com.madongfang.repository.ManagerRepository;

@Service
public class AreaService {

	public List<AreaApi> getAreas() {
		List<AreaApi> areas = new LinkedList<AreaApi>();
		
		for (Area area : areaRepository.findAll()) {
			AreaApi areaApi = new AreaApi();
			areaApi.setCardPassword(area.getCardPassword());
			areaApi.setId(area.getId());
			areaApi.setName(area.getName());
			areaApi.setServiceNumber(area.getServiceNumber());
			
			areas.add(areaApi);
		}
		
		return areas;
	}
	
	public Page<AreaApi> getAreas(int managerId, Pageable pageable) {
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-1, "管理员不存在"));
		}
		Page<Area> areas;
		if (manager.getLevel() > 1)
		{
			List<Integer> areaIds = new LinkedList<>();
			for (Area area : manager.getAreas()) {
				areaIds.add(area.getId());
			}
			if (areaIds.size() == 0)
			{
				throw new HttpBadRequestException(new ReturnApi(-2, "该管理员没有对应管理的小区"));
			}
			areas = areaRepository.findByIdIn(areaIds, pageable);
		}
		else
		{
			areas = areaRepository.findAll(pageable);
		}
		
		return areas.map(new Converter<Area, AreaApi>() {

			@Override
			public AreaApi convert(Area area) {
				// TODO Auto-generated method stub
				AreaApi areaApi = new AreaApi();
				areaApi.setCardPassword(area.getCardPassword());
				areaApi.setId(area.getId());
				areaApi.setName(area.getName());
				areaApi.setServiceNumber(area.getServiceNumber());
				areaApi.setDeviceNumber(deviceRepository.getDeviceNumber(area.getId()));
				
				return areaApi;
			}
		});
	}
	
	public AreaApi getArea(int areaId) {
		Area area = areaRepository.findOne(areaId);
		if (area == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "小区不存在"));
		}
		
		AreaApi areaApi = new AreaApi();
		areaApi.setCardPassword(area.getCardPassword());
		areaApi.setId(area.getId());
		areaApi.setName(area.getName());
		areaApi.setServiceNumber(area.getServiceNumber());
		
		return areaApi;
	}
	
	public AreaApi addArea(int managerId, AreaApi areaApi) {
		if (areaRepository.findByName(areaApi.getName()) != null)
		{
			throw new HttpBadRequestException(new ReturnApi(-2, "小区名称与已存在的小区重复"));
		}
		
		Area area = new Area();
		if (areaApi.getCardPassword() != null)
		{
			area.setCardPassword(areaApi.getCardPassword());
		}
		else
		{
			area.setCardPassword(cardPassword);
		}
		area.setName(areaApi.getName());
		area.setServiceNumber(areaApi.getServiceNumber());
		area = areaRepository.save(area);
		
		for (Manager manager : managerRepository.findByLevel(1)) // 1级管理员自动添加新增小区
		{
			if (manager.getId() != managerId)
			{
				manager.getAreas().add(area); 
				managerRepository.save(manager);
			}
		}
		
		Manager manager = managerRepository.findOne(managerId);
		if (manager != null)
		{
			manager.getAreas().add(area); 
			managerRepository.save(manager);
		}
		
		areaApi.setCardPassword(area.getCardPassword());
		areaApi.setId(area.getId());
		areaApi.setName(area.getName());
		areaApi.setServiceNumber(area.getServiceNumber());
		
		return areaApi;
	}
	
	public AreaApi updateArea(int areaId, AreaApi areaApi) {
		Area area = areaRepository.findOne(areaId);
		if (area == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-1, "更新小区不存在"));
		}
		
		if (areaApi.getCardPassword() != null)
		{
			area.setCardPassword(areaApi.getCardPassword());
		}
		
		if (areaApi.getName() != null)
		{
			area.setName(areaApi.getName());
		}
		
		if (areaApi.getServiceNumber() != null)
		{
			area.setServiceNumber(areaApi.getServiceNumber());
		}
		area = areaRepository.save(area);
		
		areaApi.setCardPassword(area.getCardPassword());
		areaApi.setId(area.getId());
		areaApi.setName(area.getName());
		areaApi.setServiceNumber(area.getServiceNumber());
		
		return areaApi;
	}
	
	public void deleteArea(int areaId) {
		if (deviceRepository.getDeviceNumber(areaId) != 0)
		{
			throw new HttpNotAcceptableException(new ReturnApi(-1, "有设备属于该小区，无法删除！"));
		}
		Area area = areaRepository.findOne(areaId);
		if (area != null && area.getManagers() != null)
		{
			List<Manager> managers = area.getManagers();
			for (Manager manager : managers) {
				List<Area> areas = manager.getAreas();
				for (int i = 0; i < areas.size(); i++) {
					if (areas.get(i).getId() == area.getId())
					{
						areas.remove(i);
						break;
					}
				}
				managerRepository.save(manager);
			}
		}
		areaRepository.delete(areaId);
	}
	
	public void updateAreaDevices(int areaId, DeviceApi deviceApi) {
		List<Device> devices = deviceRepository.findByAreaId(areaId);
		
		for (Device device : devices) {
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
		}
		
		deviceRepository.save(devices);
	}
	
	public DeviceApi getDeviceParam(int areaId) {
		Device device = deviceRepository.findFirstByAreaId(areaId);
		if (device == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "该小区不存在任何设备！"));
		}
		
		DeviceApi deviceApi = new DeviceApi();
		deviceApi.setAttachPrice(device.getAttachPrice());
		deviceApi.setFactor(device.getFactor());
		deviceApi.setFloatChargeTime(device.getFloatChargeTime());
		deviceApi.setMinPrice(device.getMinPrice());
		deviceApi.setOverdraft(device.getOverdraft());
		deviceApi.setUnitPrice(device.getUnitPrice());
		
		return deviceApi;
	}
	
	@Value("${area.cardPassword:FFFFFFFFFFFF}")
	private String cardPassword;
	
	@Autowired
	private AreaRepository areaRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private ManagerRepository managerRepository;
}
