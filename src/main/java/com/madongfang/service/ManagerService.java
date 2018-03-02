package com.madongfang.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.madongfang.api.AreaApi;
import com.madongfang.api.DeviceApi;
import com.madongfang.api.ManagerApi;
import com.madongfang.api.PlugApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Area;
import com.madongfang.entity.Device;
import com.madongfang.entity.InviteCode;
import com.madongfang.entity.Manager;
import com.madongfang.entity.Plug;
import com.madongfang.exception.HttpBadRequestException;
import com.madongfang.exception.HttpNotAcceptableException;
import com.madongfang.exception.HttpNotFoundException;
import com.madongfang.repository.AreaRepository;
import com.madongfang.repository.DeviceRepository;
import com.madongfang.repository.InviteCodeRepository;
import com.madongfang.repository.ManagerRepository;
import com.madongfang.repository.PlugRepository;
import com.madongfang.util.CommonUtil;
import com.madongfang.util.WechatUtil;

@Service
public class ManagerService {

	public ManagerApi getManager(int managerId) {
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "不存在的管理员"));
		}
		
		return convertManager(manager);
	}
	
	public ManagerApi updateManager(int managerId, ManagerApi managerApi)
	{
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "不存在的管理员"));
		}
		
		if (managerApi.getRemark() != null)
		{
			manager.setRemark(managerApi.getRemark());
		}
		managerRepository.save(manager);
		
		return convertManager(manager);
	}
	
	public Manager getManager(String username) {
		return managerRepository.findByUsername(username);
	}
	
	public List<ManagerApi> getManagers() {
		List<ManagerApi> managers = new LinkedList<ManagerApi>();
		
		for (Manager manager : managerRepository.findAll()) {
			ManagerApi managerApi = convertManager(manager);
			
			managers.add(managerApi);
		}
		
		return managers;
	}
	
	public Manager oauthLogin(WechatUtil.UserInfo userInfo) {
		Manager manager = managerRepository.findByOpenId(userInfo.getOpenid());
		if (manager == null)
		{
			logger.info("管理员不存在，openId={}", userInfo.getOpenid());
			return null;
		}
		
		manager.setNickname(userInfo.getNickname());
		managerRepository.save(manager);
		
		return manager;
	}
	
	public Manager register(String openId, String nickname, String username, String inviteCode) {
		Manager manager = managerRepository.findByOpenId(openId);
		if (manager != null)
		{
			throw new HttpNotAcceptableException(new ReturnApi(-3, "您已经是管理员了，请勿重复注册！"));
		}
		
		InviteCode inviteCodeEntity = inviteCodeRepository.findOne(inviteCode);
		if (inviteCodeEntity == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-4, "无效的邀请码！"));
		}
		
		if (inviteCodeEntity.getUsed())
		{
			throw new HttpBadRequestException(new ReturnApi(-5, "该邀请码已被使用！"));
		}
		inviteCodeEntity.setUsed(true);
		inviteCodeRepository.save(inviteCodeEntity);
		
		manager = new Manager();
		manager.setLevel(inviteCodeEntity.getLevel());
		manager.setNickname(nickname);
		manager.setOpenId(openId);
		manager.setUsername(username);
		if (inviteCodeEntity.getLevel() <= 1) // 一级管理员自动添加当前所有的小区
		{
			manager.setAreas(areaRepository.findAll());
		}
		
		managerRepository.save(manager);
		
		return manager;
	}
	
	public String getLoginCode(String openId) {
		Manager manager = managerRepository.findByOpenId(openId);
		if (manager == null)
		{
			logger.info("管理员不存在，openId={}", openId);
			return null;
		}
		
		manager.setGenerateTime(new Date());
		String loginCode = commonUtil.getRandomStringByLength(6);
		manager.setLoginCode(commonUtil.md5(loginCode).toLowerCase());
		managerRepository.save(manager);
		
		return loginCode;
	}
	
	public List<AreaApi> getAreas(int managerId) {
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpNotFoundException(new ReturnApi(-1, "管理员不存在"));
		}
		
		List<AreaApi> areas = new LinkedList<AreaApi>();
		for (Area area : manager.getAreas()) {
			AreaApi areaApi = new AreaApi();
			areaApi.setCardPassword(area.getCardPassword());
			areaApi.setId(area.getId());
			areaApi.setName(area.getName());
			
			areas.add(areaApi);
		}
		
		return areas;
	}
	
	public ReturnApi setAreas(int managerId, List<AreaApi> areas)
	{
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpNotFoundException(new ReturnApi(-1, "管理员不存在"));
		}
		
		List<Integer> areaIds = new LinkedList<>();
		for (AreaApi areaApi : areas) {
			areaIds.add(areaApi.getId());
		}
		manager.setAreas(areaRepository.findByIdIn(areaIds));
		managerRepository.save(manager);
		
		return new ReturnApi(0, "OK");
	}
	
	public List<DeviceApi> getAreaDevices(int managerId, Integer areaId) {
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpNotFoundException(new ReturnApi(-1, "管理员不存在"));
		}
		
		boolean validArea = false;
		for (Area area : manager.getAreas())
		{
			if (area.getId() == areaId)
			{
				validArea = true;
				break;
			}
		}
		if (!validArea)
		{
			throw new HttpNotAcceptableException(new ReturnApi(-2, "该区域不属于该管理员"));
		}
		
		List<DeviceApi> devices = new LinkedList<DeviceApi>();
		for (Device device : deviceRepository.findByAreaId(areaId)) {
			DeviceApi deviceApi = new DeviceApi();
			deviceApi.setCode(device.getCode());
			deviceApi.setLocation(device.getName());
			devices.add(deviceApi);
		}
		
		return devices;
	}
	
	public List<PlugApi> getDevicePlugs(int managerId, String deviceCode)
	{
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			logger.warn("管理员不存在:managerId={}", managerId);
			throw new HttpNotFoundException(new ReturnApi(-1, "管理员不存在"));
		}
		
		Device device = deviceRepository.findByCode(deviceCode);
		if (device == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-2, "设备不存在"));
		}
		if (device.getArea() == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-3, "设备无所属小区"));
		}

		int areaId = device.getArea().getId();
		boolean validArea = false;
		for (Area area : manager.getAreas())
		{
			if (area.getId() == areaId)
			{
				validArea = true;
				break;
			}
		}
		if (!validArea)
		{
			throw new HttpNotAcceptableException(new ReturnApi(-4, "该设备不属于该管理员管辖的小区"));
		}
		
		List<PlugApi> plugs = new LinkedList<PlugApi>();
		for (Plug plug : plugRepository.findByDeviceCodeOrderByIdAsc(deviceCode)) {
			PlugApi plugApi = new PlugApi();
			plugApi.setId(plug.getId());
			if ("E".equals(plug.getStatus())) // 插座故障
			{
				plugApi.setStatus("E");
			}
			else
			{
				if (plug.getInUse())
				{
					plugApi.setStatus("U");
				}
				else 
				{
					plugApi.setStatus("F");
				}
			}
			if (plugApi.getStatus().equals("U"))
			{
				plugApi.setConsumePower(plug.getConsumePower());
				plugApi.setCustomId(plug.getCustom().getId());
				plugApi.setLimitPrice(plug.getLimitPrice());
				plugApi.setNickname(plug.getCustom().getNickname());
				plugApi.setPower(plug.getPower());
				plugApi.setStartTime(plug.getStartTime());
				plugApi.setUpdateTime(plug.getUpdateTime());
			}
			plugs.add(plugApi);
		}
		
		return plugs;
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private ManagerRepository managerRepository;
	
	@Autowired
	private InviteCodeRepository inviteCodeRepository;
	
	@Autowired
	private DeviceRepository deviceRepository;
	
	@Autowired
	private PlugRepository plugRepository;
	
	@Autowired
	private AreaRepository areaRepository;
	
	private ManagerApi convertManager(Manager manager)
	{
		ManagerApi managerApi = new ManagerApi();
		managerApi.setId(manager.getId());
		managerApi.setLevel(manager.getLevel());
		managerApi.setNickname(manager.getNickname());
		managerApi.setUsername(manager.getUsername());
		managerApi.setRemark(manager.getRemark());
		
		return managerApi;
	}
}
