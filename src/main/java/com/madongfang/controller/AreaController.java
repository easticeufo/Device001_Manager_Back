package com.madongfang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.madongfang.api.AreaApi;
import com.madongfang.api.DeviceApi;
import com.madongfang.entity.Manager;
import com.madongfang.service.AreaService;

@RestController
@RequestMapping(value="/api/areas")
public class AreaController {

	@GetMapping
	public List<AreaApi> getAreas()
	{
		return areaService.getAreas();
	}
	
	@GetMapping(params="page")
	public Page<AreaApi> getAreas(@SessionAttribute Manager manager, @PageableDefault(size=30) Pageable pageable)
	{
		return areaService.getAreas(manager.getId(), pageable);
	}
	
	@GetMapping(value="/{areaId}")
	public AreaApi getArea(@PathVariable int areaId)
	{
		return areaService.getArea(areaId);
	}
	
	@PostMapping
	public AreaApi addArea(@SessionAttribute Manager manager, @RequestBody AreaApi areaApi)
	{
		return areaService.addArea(manager.getId(), areaApi);
	}
	
	@PutMapping(value="/{areaId}")
	public AreaApi updateArea(@PathVariable int areaId, @RequestBody AreaApi areaApi)
	{
		return areaService.updateArea(areaId, areaApi);
	}
	
	@DeleteMapping(value="/{areaId}")
	public void deleteArea(@PathVariable int areaId)
	{
		areaService.deleteArea(areaId);
	}
	
	@PutMapping(value="/{areaId}/devices")
	public void updateAreaDevices(@PathVariable int areaId, @RequestBody DeviceApi deviceApi)
	{
		areaService.updateAreaDevices(areaId, deviceApi);
	}
	
	@GetMapping(value="/{areaId}/deviceParam")
	public DeviceApi getDeviceParam(@PathVariable int areaId)
	{
		return areaService.getDeviceParam(areaId);
	}
	
	@Autowired
	private AreaService areaService;
}
