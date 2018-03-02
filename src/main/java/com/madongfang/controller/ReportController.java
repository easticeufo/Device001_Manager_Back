package com.madongfang.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.madongfang.api.ReportApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.data.ReportExcel;
import com.madongfang.entity.Manager;
import com.madongfang.exception.HttpInternalServerErrorException;
import com.madongfang.service.ReportService;
import com.madongfang.util.ExcelUtil;

@RestController
@RequestMapping(value="/api/report")
public class ReportController {

	@GetMapping(value="/year/{year}")
	public List<ReportApi> getAnnualReport(@SessionAttribute Manager manager, 
			@PathVariable int year, 
			@RequestParam(required=false) Integer areaId,
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) Integer plugId)
	{
		return reportService.getAnnualReport(manager.getId(), year, areaId, deviceCode, plugId);
	}
	
	@GetMapping(value="/year/{year}/month/{month}")
	public List<ReportApi> getMonthlyReport(@SessionAttribute Manager manager, 
			@PathVariable int year, 
			@PathVariable int month, 
			@RequestParam(required=false) Integer areaId,
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) Integer plugId)
	{
		return reportService.getMonthlyReport(manager.getId(), year, month, areaId, deviceCode, plugId);
	}
	
	@GetMapping(value="/year/{year}/excel")
	public String exportAnnualReportExcel(HttpServletResponse response, 
			@SessionAttribute Manager manager, 
			@PathVariable int year, 
			@RequestParam(required=false) Integer areaId,
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) Integer plugId)
	{
		List<ReportApi> report = reportService.getAnnualReport(manager.getId(), year, areaId, deviceCode, plugId);
		
		try {
			List<ReportExcel> excelReport = reportConvertToExcel(report);
			
			response.setContentType("application/binary;charset=ISO8859_1");
			String filename = new String("报表.xlsx".getBytes(), "ISO8859_1");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			String[] titles = {"时间", "充电次数", "充电时长", "充电度数(度)", "收入(元)"};
			excelUtil.export(response.getOutputStream(), titles, excelReport);
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("导出excel异常:", e);
			throw new HttpInternalServerErrorException(new ReturnApi(-1, "导出excel失败"));
		}
		
		return null;
	}
	
	@GetMapping(value="/year/{year}/month/{month}/excel")
	public String exportMonthlyReportExcel(HttpServletResponse response, 
			@SessionAttribute Manager manager, 
			@PathVariable int year, 
			@PathVariable int month, 
			@RequestParam(required=false) Integer areaId,
			@RequestParam(required=false) String deviceCode,
			@RequestParam(required=false) Integer plugId)
	{
		List<ReportApi> report = reportService.getMonthlyReport(manager.getId(), year, month, areaId, deviceCode, plugId);
		
		try {
			List<ReportExcel> excelReport = reportConvertToExcel(report);
			
			response.setContentType("application/binary;charset=ISO8859_1");
			String filename = new String("报表.xlsx".getBytes(), "ISO8859_1");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			String[] titles = {"时间", "充电次数", "充电时长", "充电度数(度)", "收入(元)"};
			excelUtil.export(response.getOutputStream(), titles, excelReport);
		} catch (IllegalArgumentException | IllegalAccessException | IOException e) {
			// TODO Auto-generated catch block
			logger.error("导出excel异常:", e);
			throw new HttpInternalServerErrorException(new ReturnApi(-1, "导出excel失败"));
		}
		
		return null;
	}
	
	@Autowired
	private ReportService reportService;
	
	@Autowired
	private ExcelUtil excelUtil;
	
	private List<ReportExcel> reportConvertToExcel(List<ReportApi> report)
	{
		SimpleDateFormat sdf = new SimpleDateFormat();
		List<ReportExcel> excelReport = new LinkedList<ReportExcel>();
		for (ReportApi reportApi : report) {
			ReportExcel item = new ReportExcel();
			item.setChargeNumber(reportApi.getChargeNumber());
			int durationTime = reportApi.getDurationTime();
			int mins = durationTime / 60;
			int hour = mins / 60;
			int min = mins % 60;
			item.setDurationTime(String.format("%d小时%d分钟", hour, min));
			item.setIncome((float)reportApi.getIncome() / 100);
			item.setPowerConsumption((float)reportApi.getPowerConsumption() / 1000);
			switch (reportApi.getType()) {
			case "year":
				sdf.applyPattern("yyyy年");
				break;
				
			case "month":
				sdf.applyPattern("yyyy年M月");
				break;
				
			case "date":
				sdf.applyPattern("yyyy年M月d日");
				break;
				
			default:
				sdf.applyPattern("错误:type="+reportApi.getType());
				break;
			}
			item.setTime(sdf.format(reportApi.getStartTime()));
			excelReport.add(0, item);
		}
		
		return excelReport;
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
}
