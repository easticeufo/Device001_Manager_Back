package com.madongfang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.PlugApi;
import com.madongfang.service.DeviceService;

@RestController
@RequestMapping(value="/api/devices/{deviceCode}/plugs")
public class PlugController {

	@PutMapping(value="/{plugId}")
	public PlugApi setPlugStatus(@PathVariable String deviceCode, @PathVariable Integer plugId, @RequestBody PlugApi plugApi) {
		return deviceService.setPlugStatus(deviceCode, plugId, plugApi.getStatus());
	}
	
	@Autowired
	private DeviceService deviceService;
}
