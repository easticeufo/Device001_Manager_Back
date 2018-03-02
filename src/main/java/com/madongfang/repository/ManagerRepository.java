package com.madongfang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.madongfang.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Integer> {

	public Manager findByUsername(String username);
	
	public Manager findByOpenId(String openId);
	
	public List<Manager> findByLevel(int level);
}
