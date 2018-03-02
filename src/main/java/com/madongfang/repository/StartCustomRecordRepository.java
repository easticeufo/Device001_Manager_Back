package com.madongfang.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.Device;
import com.madongfang.entity.StartCustomRecord;

public interface StartCustomRecordRepository extends JpaRepository<StartCustomRecord, Integer> {
	
	public StartCustomRecord findByDeviceCodeAndPlugIdAndCustomRecordIsNull(String deviceCode, Integer plugId);
	
	@Modifying
	@Query("update StartCustomRecord r set r.device=?1 where r.device=?2")
	public int updateDevice(Device newDevice, Device oldDevice);
	
	@Query("select sum(d.amount) from StartCustomRecord d where d.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
}
