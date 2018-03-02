package com.madongfang.controller;

import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.madongfang.api.FinanceApi;
import com.madongfang.service.FinanceService;

@RestController
@RequestMapping(value="/api/finance")
public class FinanceController {

	@GetMapping(value="/year/{year}/month/{month}")
	public FinanceApi getMonthlyFinance(@PathVariable int year, @PathVariable int month)
	{
		month--; // 0表示1月
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date startTime = calendar.getTime();
		calendar.set(Calendar.MONTH, month + 1);
		Date stopTime = new Date(calendar.getTimeInMillis() - 1);
		
		return financeService.getFinance(startTime, stopTime);
	}
	
	@Autowired
	private FinanceService financeService;
}
