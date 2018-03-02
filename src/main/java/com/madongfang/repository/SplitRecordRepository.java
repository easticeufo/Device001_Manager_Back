package com.madongfang.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.api.ManagerSplitApi;
import com.madongfang.entity.SplitRecord;

public interface SplitRecordRepository extends JpaRepository<SplitRecord, Integer> {

	@Query("select new com.madongfang.api.ManagerSplitApi(m.nickname,m.remark,sum(r.amount)) from SplitRecord r, Manager m where r.managerId=m.id and r.isSuccess=true and r.time between ?1 and ?2 group by r.managerId")
	public List<ManagerSplitApi> getManagerSplitsByTimeBetween(Date startTime, Date stopTime);
	
	public List<SplitRecord> findByIsSuccessFalseAndTimeAfter(Date time);
}
