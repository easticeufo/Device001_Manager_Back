package com.madongfang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.madongfang.service.DeviceManagerService;

@Component
public class ScheduledTasks {

	@Scheduled(cron="00 05 00 * * *")
	public void splitMoney() {
		logger.info("开始分账");
		deviceManagerService.splitMoney();
	}
	
	@Scheduled(cron="00 10 * * * *")
	public void checkSplitMoney() {
		logger.debug("检查分账");
		deviceManagerService.checkSplitMoney();
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private DeviceManagerService deviceManagerService;
}
