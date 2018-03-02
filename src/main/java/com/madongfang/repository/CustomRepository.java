package com.madongfang.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.madongfang.entity.Custom;

public interface CustomRepository extends JpaRepository<Custom, Integer> {

	public Custom findByTypeAndUserOpenId(String type, String userOpenId);
	
	public Custom findByTypeAndNickname(String type, String nickname);
	
	public Custom findByTypeAndUnionid(String type, String unionid);
	
	public Custom findByPhoneNumber(String phoneNumber);
	
	public Page<Custom> findByNicknameContaining(String nickname, Pageable pageable);
	
	@Query("select sum(c.balance) from Custom c")
	public Integer getTotalBalance();
	
	@Query("select sum(c.balance) from Custom c where c.nickname like %?1%")
	public Integer getTotalBalance(String nickname);
}
