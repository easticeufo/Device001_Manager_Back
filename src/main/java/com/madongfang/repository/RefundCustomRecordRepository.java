package com.madongfang.repository;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.RefundCustomRecord;

public interface RefundCustomRecordRepository extends JpaRepository<RefundCustomRecord, Integer> {

	@Query("select sum(t.amount) from RefundCustomRecord t where t.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
}
