package com.madongfang.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.madongfang.entity.InviteCode;

public interface InviteCodeRepository extends JpaRepository<InviteCode, String> {

	public List<InviteCode> findByLevelAndUsedFalse(int level);
}
