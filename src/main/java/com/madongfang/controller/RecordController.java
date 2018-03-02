package com.madongfang.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.ChargeRecordApi;
import com.madongfang.service.CustomRecordService;

@RestController
@RequestMapping(value="/api/records")
public class RecordController {

	@GetMapping(value="/charge", params="page")
	public Page<ChargeRecordApi> getChargeRecords(
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) List<Integer> areaIds,
			@RequestParam(required=false) String location,
			@RequestParam(required=false) Long startTime,
			@RequestParam(required=false) Long stopTime,
			@PageableDefault(size=30) Pageable pageable) 
	{
		logger.debug("getChargeRecords:deviceCode={}, areaIds={}, location={}, startTime={}, stopTime={}", 
				deviceCode, areaIds, location, startTime, stopTime);
		return customRecordService.getChargeRecords(deviceCode, areaIds, location, startTime, stopTime, pageable);
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private CustomRecordService customRecordService;
}
