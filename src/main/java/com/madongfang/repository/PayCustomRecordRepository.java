package com.madongfang.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.PayCustomRecord;

public interface PayCustomRecordRepository extends JpaRepository<PayCustomRecord, Integer> {

	public Page<PayCustomRecord> findByCustomId(Integer customId, Pageable pageable);
	
	@Query("select sum(q.amount) from PayCustomRecord q where q.time between ?1 and ?2")
	public Integer getSumAmountByTimeBetween(Date startTime, Date stopTime);
	
	@Query("select sum(q.amount) from PayCustomRecord q where q.tradeNumber like 'W%' and q.time between ?1 and ?2")
	public Integer getWechatAmountByTimeBetween(Date startTime, Date stopTime);
	
	@Query("select sum(q.amount) from PayCustomRecord q where q.tradeNumber like 'A%' and q.time between ?1 and ?2")
	public Integer getAlipayAmountByTimeBetween(Date startTime, Date stopTime);
	
	@Query("select sum(q.amount) from PayCustomRecord q where q.tradeNumber like 'M%' and q.time between ?1 and ?2")
	public Integer getManualAmountByTimeBetween(Date startTime, Date stopTime);
}
