package com.madongfang.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.DeviceManager;
import com.madongfang.entity.DeviceManagerPK;

public interface DeviceManagerRepository extends JpaRepository<DeviceManager, DeviceManagerPK> {

	@Query("select dm.deviceCode from DeviceManager dm group by dm.deviceCode having sum(dm.splitPercent) <= 100")
	public List<String> getSplitDeviceCodes();
	
	public List<DeviceManager> findByDeviceCodeIn(Collection<String> deviceCodes);
}
