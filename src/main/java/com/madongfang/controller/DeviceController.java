package com.madongfang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.DeviceApi;
import com.madongfang.api.DevicePromptApi;
import com.madongfang.service.DeviceService;

@RestController
@RequestMapping(value="/api/devices")
public class DeviceController {

	@PostMapping
	public DeviceApi addDevice(@RequestBody DeviceApi deviceApi) {
		return deviceService.addDevice(deviceApi);
	}
	
	@GetMapping
	public List<DeviceApi> getDevices() {
		return deviceService.getDevices();
	}
	
	@PostMapping(value="/{deviceCode}/repair")
	public DeviceApi repairDevice(@PathVariable String deviceCode)
	{
		return deviceService.repairDevice(deviceCode);
	}
	
	@GetMapping(value="/{deviceCode}/prompts")
	public List<DevicePromptApi> getPrompts(@PathVariable String deviceCode)
	{
		return deviceService.getPrompts(deviceCode);
	}

	@Autowired
	private DeviceService deviceService;
}
