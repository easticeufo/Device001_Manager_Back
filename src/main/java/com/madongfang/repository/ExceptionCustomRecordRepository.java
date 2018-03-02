package com.madongfang.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.ExceptionCustomRecord;

public interface ExceptionCustomRecordRepository extends JpaRepository<ExceptionCustomRecord, Integer> {

	@Query("select sum(e.amount) from ExceptionCustomRecord e where e.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
}
