package com.madongfang.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.StopCustomRecord;

public interface StopCustomRecordRepository extends JpaRepository<StopCustomRecord, Integer> {

	public StopCustomRecord findFirstByCustomIdOrderByTimeDesc(Integer customId);
	
	public Page<StopCustomRecord> findByCustomId(Integer customId, Pageable pageable);
	
	@Query("select sum(p.amount) from StopCustomRecord p where p.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
}
