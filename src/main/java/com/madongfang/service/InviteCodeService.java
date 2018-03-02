package com.madongfang.service;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.madongfang.entity.InviteCode;
import com.madongfang.repository.InviteCodeRepository;
import com.madongfang.util.CommonUtil;

@Service
public class InviteCodeService {

	public List<String> getInviteCodes(int level, int number) {
		List<String> inviteCodes = new LinkedList<>();
		List<InviteCode> inviteCodeList = inviteCodeRepository.findByLevelAndUsedFalse(level);
		while (inviteCodeList.size() < number)
		{
			InviteCode inviteCode = new InviteCode();
			inviteCode.setInviteCode(commonUtil.getRandomStringByLength(6));
			inviteCode.setLevel(level);
			inviteCode.setUsed(false);
			inviteCodeRepository.save(inviteCode);
			inviteCodeList.add(inviteCode);
		}
		
		for (InviteCode inviteCode : inviteCodeList) {
			inviteCodes.add(inviteCode.getInviteCode());
			if (inviteCodes.size() >= number)
			{
				break;
			}
		}
		
		return inviteCodes;
	}
	
	@Autowired
	private InviteCodeRepository inviteCodeRepository;
	
	@Autowired
	private CommonUtil commonUtil;
}
