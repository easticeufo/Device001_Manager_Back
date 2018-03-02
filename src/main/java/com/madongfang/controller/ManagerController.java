package com.madongfang.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.AreaApi;
import com.madongfang.api.ManagerApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.service.ManagerService;

@RestController
@RequestMapping(value="/api/managers")
public class ManagerController {

	@GetMapping
	public List<ManagerApi> getManagers() {
		return managerService.getManagers();
	}
	
	@GetMapping(value="/{managerId}")
	public ManagerApi getManager(@PathVariable int managerId) {
		return managerService.getManager(managerId);
	}
	
	@PutMapping(value="/{managerId}")
	public ManagerApi updateManager(@PathVariable int managerId, @RequestBody ManagerApi managerApi) {
		return managerService.updateManager(managerId, managerApi);
	}
	
	@GetMapping(value="/{managerId}/areas")
	public List<AreaApi> getAreas(@PathVariable int managerId)
	{
		return managerService.getAreas(managerId);
	}
	
	@PutMapping(value="/{managerId}/areas")
	public ReturnApi setAreas(@PathVariable int managerId, @RequestBody List<AreaApi> areas)
	{
		return managerService.setAreas(managerId, areas);
	}
	
	@Autowired
	private ManagerService managerService;
}
