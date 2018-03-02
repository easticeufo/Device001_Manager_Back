package com.madongfang.util;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelUtil {

	public void export(OutputStream out, String[] titles, List<?> items) throws IllegalArgumentException, IllegalAccessException, IOException {
		XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
		XSSFCellStyle css = xssfWorkbook.createCellStyle();
		XSSFDataFormat format = xssfWorkbook.createDataFormat();
		css.setDataFormat(format.getFormat("@"));
		XSSFCell cell = null;
		
		XSSFSheet sheet = xssfWorkbook.createSheet();
		
		/* 生成表头字段 */
		XSSFRow row = sheet.createRow(0);
		for (int i = 0; i < titles.length; i++)
		{
			row.createCell(i).setCellValue(titles[i]);
		}
		
		/* 生成表内容字段 */
		for (int i = 0; i < items.size(); i++) {
			Object item = items.get(i);
			Field[] fields= item.getClass().getDeclaredFields();
			row = sheet.createRow(i+1);
			for (int j = 0; j < fields.length; j++)
			{
				fields[j].setAccessible(true);
				
				String fieldType = fields[j].getType().getSimpleName();
				if ("String".equals(fieldType))
				{
					cell = row.createCell(j, XSSFCell.CELL_TYPE_STRING);
					cell.setCellStyle(css);
					cell.setCellValue((String)fields[j].get(item));
				}
				else if ("Integer".equals(fieldType))
				{
					cell = row.createCell(j, XSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Integer)fields[j].get(item));
				}
				else if ("Double".equals(fieldType))
				{
					cell = row.createCell(j, XSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Double)fields[j].get(item));
				}
				else if ("Float".equals(fieldType))
				{
					cell = row.createCell(j, XSSFCell.CELL_TYPE_NUMERIC);
					cell.setCellValue((Float)fields[j].get(item));
				}
			}
		}
		
		xssfWorkbook.write(out);
		xssfWorkbook.close();
	}
	
}
