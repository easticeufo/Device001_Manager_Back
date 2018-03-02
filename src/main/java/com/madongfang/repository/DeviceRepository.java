package com.madongfang.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.Area;
import com.madongfang.entity.Device;

public interface DeviceRepository extends JpaRepository<Device, String> {

	public Device findByCode(String deviceCode);
	
	@Query("select d.code from Device d where d.code like ?1%")
	public List<String> getDeviceCodesLike(String deviceCode);
	
	public Page<Device> findByAreaInAndCodeContainingAndNameContaining(Collection<Area> areas, String deviceCode, String deviceName, Pageable pageable);
	
	public List<Device> findByAreaIn(Collection<Area> areas);
	
	public List<Device> findByAreaId(Integer areaId);
	
	public List<Device> findByCodeIn(Collection<String> deviceCodes);
	
	public Device findFirstByAreaId(Integer areaId);
	
	@Query("select count(*) from Device d where d.area.id=?1")
	public int getDeviceNumber(Integer areaId);
	
	@Query("select d.code from Device d where d.area in ?1")
	public List<String> getDeviceCodes(Collection<Area> areas);
	
	@Query("select count(d) from Device d where d.area in ?1 and d.code like %?2% and d.name like %?3% and d.aliveTime > ?4")
	public int getAliveDeviceNumberInAreas(Collection<Area> areas, String deviceCode, String deviceName, Date minTime);

}
