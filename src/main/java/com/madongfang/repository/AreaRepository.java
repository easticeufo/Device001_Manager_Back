package com.madongfang.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.madongfang.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Integer> {

	public Area findByName(String name);
	
	public List<Area> findByIdIn(Collection<Integer> areaIds);
	
	public Page<Area> findByIdIn(Collection<Integer> areaIds, Pageable pageable);
}
