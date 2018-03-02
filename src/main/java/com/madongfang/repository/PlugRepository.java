package com.madongfang.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.Area;
import com.madongfang.entity.Plug;
import com.madongfang.entity.PlugPK;

public interface PlugRepository extends JpaRepository<Plug, PlugPK> {

	public List<Plug> findByDeviceCodeOrderByIdAsc(String deviceCode);
	
	public List<Plug> findByCustomIdAndInUseTrueOrderByStartTimeDesc(Integer customId);
	
	@Query("select count(p) from Plug p where p.deviceCode=?1 and p.inUse=true")
	public int getInusePlugNumber(String deviceCode);
	
	@Query("select new com.madongfang.repository.InusePlugInfo(count(p),sum(p.consumePower)) from Plug p where " 
	+ "p.inUse=true and p.deviceCode in (select d.code from Device d where d.area in ?1 and d.code like %?2% and d.name like %?3%)")
	public InusePlugInfo getInusePlugInfo(Collection<Area> areas, String deviceCode, String deviceName);
}
