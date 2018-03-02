package com.madongfang.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.Area;
import com.madongfang.entity.CustomRecord;
import com.madongfang.entity.Device;

public interface CustomRecordRepository extends JpaRepository<CustomRecord, Integer> {

	public Page<CustomRecord> findByCustomId(Integer customId, Pageable pageable);
	
	@Query("select new com.madongfang.repository.ReportRecord(d.time,p.time,d.amount,p.amount,p.powerConsumption) from StopCustomRecord p join p.startCustomRecord d where p.time between ?1 and ?2 and d.device in ?3")
	public List<ReportRecord> findReportByTimeBetweenAndDeviceIn(Date startTime, Date stopTime, Collection<Device> devices);
	
	@Query("select new com.madongfang.repository.ReportRecord(d.time,p.time,d.amount,p.amount,p.powerConsumption) from StopCustomRecord p join p.startCustomRecord d where p.time between ?1 and ?2 and d.device.code=?3")
	public List<ReportRecord> findReportByTimeBetweenAndDeviceCode(Date startTime, Date stopTime, String deviceCode);
	
	@Query("select new com.madongfang.repository.ReportRecord(d.time,p.time,d.amount,p.amount,p.powerConsumption) from StopCustomRecord p join p.startCustomRecord d where p.time between ?1 and ?2 and d.device.code=?3 and d.plugId=?4")
	public List<ReportRecord> findReportByTimeBetweenAndDeviceCodeAndPlugId(Date startTime, Date stopTime, String deviceCode, Integer plugId);
	
	@Query("select new com.madongfang.repository.ChargeRecord(d.plugId,d.time,p.time,d.amount,p.amount,p.powerConsumption,d.device.code,d.device.area.name,d.device.name,d.custom.id,d.custom.nickname) " 
	+ "from StopCustomRecord p join p.startCustomRecord d where d.device.code like ?1 and d.device.area in ?2 and d.device.name like ?3 and p.time between ?4 and ?5 order by p.time desc")
	public Page<ChargeRecord> findChargeRecords(String deviceCode, Collection<Area> areas, String location, Date startTime, Date stopTime, Pageable pageable);

	@Query("select new com.madongfang.repository.DeviceIncome(d.device.code, sum(-d.amount-p.amount)) from StopCustomRecord p join p.startCustomRecord d" 
	+ " where d.device.code in ?1 and p.time between ?2 and ?3 group by d.device.code")
	public List<DeviceIncome> getDeviceIncome(Collection<String> deviceCodes, Date startTime, Date stopTime);
	
	@Query("select sum(r.amount) from CustomRecord r where r.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
}
