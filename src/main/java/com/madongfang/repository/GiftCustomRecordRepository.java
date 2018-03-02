package com.madongfang.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.GiftCustomRecord;

public interface GiftCustomRecordRepository extends JpaRepository<GiftCustomRecord, Integer> {

	@Query("select sum(s.amount) from GiftCustomRecord s where s.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
}
