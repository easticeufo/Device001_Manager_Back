package com.madongfang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.CustomApi;
import com.madongfang.service.CustomService;

@RestController
@RequestMapping(value="/api/app")
public class AppController {

	@GetMapping("/custom")
	public CustomApi getCustom(@RequestParam String type, @RequestParam String unionid)
	{
		return customService.getCustom(type, unionid);
	}
	
	@Autowired
	private CustomService customService;
}
