package com.madongfang.service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.madongfang.api.CardApi;
import com.madongfang.api.CardRechargeApi;
import com.madongfang.api.CustomApi;
import com.madongfang.api.CustomPageApi;
import com.madongfang.api.RefundApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Custom;
import com.madongfang.entity.Manager;
import com.madongfang.entity.PayCustomRecord;
import com.madongfang.entity.RefundCustomRecord;
import com.madongfang.entity.WechatData;
import com.madongfang.exception.HttpBadRequestException;
import com.madongfang.exception.HttpForbiddenException;
import com.madongfang.exception.HttpNotFoundException;
import com.madongfang.repository.CustomRepository;
import com.madongfang.repository.ManagerRepository;
import com.madongfang.repository.PayCustomRecordRepository;
import com.madongfang.repository.RefundCustomRecordRepository;
import com.madongfang.repository.WechatDataRepository;
import com.madongfang.util.CommonUtil;

@Service
@Transactional(isolation=Isolation.SERIALIZABLE)
public class CustomService {
	
	public CardApi addCard(CardApi cardApi) {
		ReturnApi returnApi = new ReturnApi();
		
		Custom custom = customRepository.findByTypeAndNickname("C", cardApi.getId());
		if (custom != null)
		{
			returnApi.setReturnCode(-1);
			returnApi.setReturnMsg("该充电卡已经添加");
			throw new HttpBadRequestException(returnApi);
		}
		
		custom = new Custom();
		custom.setBalance(cardApi.getBalance());
		custom.setGenerateTime(null);
		custom.setLimitPrice(200);
		custom.setLoginCode(null);
		custom.setNickname(cardApi.getId());
		custom.setPhoneNumber(null);
		custom.setReserveDay(0);
		custom.setType("C");
		custom.setUserOpenId(cardApi.getId());
		
		customRepository.save(custom);
		
		return convertToCardApi(custom);
	}
	
	public CardApi getCard(String cardId)
	{
		Custom custom = customRepository.findByTypeAndNickname("C", cardId);
		if (custom == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "该充电卡不存在"));
		}
		
		return convertToCardApi(custom);
	}
	
	public CardApi rechargeCard(int managerId, String cardId, CardRechargeApi cardRechargeApi) {
		Custom custom = customRepository.findByTypeAndNickname("C", cardId);
		if (custom == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-1, "该充电卡不存在"));
		}
		
		if (cardRechargeApi.getAmount() <= 0)
		{
			throw new HttpBadRequestException(new ReturnApi(-2, "充值金额必须大于0"));
		}

		Date now = new Date();
		String tradeNumber = "M" + managerId + "_" + now.getTime() + "_";
		tradeNumber += commonUtil.getRandomStringByLength(32 - tradeNumber.length());
		
		custom.setBalance(custom.getBalance() + cardRechargeApi.getAmount());
		
		PayCustomRecord payCustomRecord = new PayCustomRecord();
		payCustomRecord.setAmount(cardRechargeApi.getAmount());
		payCustomRecord.setBalance(custom.getBalance());
		payCustomRecord.setCustom(custom);
		payCustomRecord.setTime(now);
		payCustomRecord.setTradeNumber(tradeNumber);
		payCustomRecordRepository.save(payCustomRecord);
		
		return convertToCardApi(custom);
	}
	
	public CustomPageApi getCustoms(String nickname, Pageable pageable)
	{
		Page<Custom> customs;
		Integer totalBalance;
		
		if (nickname == null || nickname.length() == 0)
		{
			customs = customRepository.findAll(pageable);
			totalBalance = customRepository.getTotalBalance();
		}
		else
		{
			customs = customRepository.findByNicknameContaining(nickname, pageable);
			totalBalance = customRepository.getTotalBalance(nickname);
		}
		
		List<CustomApi> content = new LinkedList<CustomApi>();
		for (Custom custom : customs) {
			content.add(convertCustom(custom));
		}
		
		CustomPageApi customPageApi = new CustomPageApi();
		customPageApi.setContent(content);
		customPageApi.setFirst(customs.isFirst());
		customPageApi.setLast(customs.isLast());
		customPageApi.setNumber(customs.getNumber());
		customPageApi.setNumberOfElements(customs.getNumberOfElements());
		customPageApi.setSize(customs.getSize());
		customPageApi.setTotalBalance(totalBalance);
		customPageApi.setTotalElements(customs.getTotalElements());
		customPageApi.setTotalPages(customs.getTotalPages());
		
		return customPageApi;
	}
	
	public CustomApi getCustom(int customId)
	{
		Custom custom = customRepository.findOne(customId);
		if (custom == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "客户不存在"));
		}
		
		return convertCustom(custom);
	}
	
	public CustomApi getCustom(String type, String unionid)
	{
		Custom custom;
		if ("C".equals(type)) // 充电卡客户
		{
			custom = customRepository.findByTypeAndNickname(type, unionid);
		}
		else if ("A".equals(type)) // 支付宝客户
		{
			custom = customRepository.findByTypeAndUserOpenId(type, unionid);
		}
		else // 微信客户
		{
			custom = customRepository.findByTypeAndUnionid(type, unionid);
		}
		
		if (custom == null)
		{
			throw new HttpNotFoundException(new ReturnApi(-1, "客户不存在"));
		}
		
		return convertCustom(custom);
	}
	
	public RefundResult refund(int managerId, int customId, RefundApi refundApi) {
		if (!refundPassword.equals(refundApi.getPassword()))
		{
			throw new HttpForbiddenException(new ReturnApi(-1, "退款密码错误"));
		}
		
		Custom custom = customRepository.findOne(customId);
		if (custom == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-2, "退款客户不存在"));
		}
		
		Manager manager = managerRepository.findOne(managerId);
		if (manager == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-3, "管理员不存在"));
		}
		
		Date now = new Date();
		
		custom.setBalance(custom.getBalance() + refundApi.getAmount());
		customRepository.save(custom);
		
		RefundCustomRecord refundCustomRecord = new RefundCustomRecord();
		refundCustomRecord.setAmount(refundApi.getAmount());
		refundCustomRecord.setBalance(custom.getBalance());
		refundCustomRecord.setCustom(custom);
		refundCustomRecord.setManager(manager);
		refundCustomRecord.setReason(refundApi.getReason());
		refundCustomRecord.setTime(now);
		refundCustomRecordRepository.save(refundCustomRecord);
		
		RefundResult refundResult = new RefundResult();
		List<WechatData> wechatDatas = wechatDataRepository.findAll();
		if (wechatDatas.size() > 0)
		{
			refundResult.setAccessToken(wechatDatas.get(0).getAccessToken());
		}
		refundResult.setCustomBalance(custom.getBalance());
		refundResult.setCustomType(custom.getType());
		refundResult.setCustomUserOpenId(custom.getUserOpenId());
		refundResult.setRefundTime(now);
		
		return refundResult;
	}
	
	public static class RefundResult
	{
		public String getCustomType() {
			return customType;
		}

		public void setCustomType(String customType) {
			this.customType = customType;
		}

		public String getCustomUserOpenId() {
			return customUserOpenId;
		}

		public void setCustomUserOpenId(String customUserOpenId) {
			this.customUserOpenId = customUserOpenId;
		}

		public int getCustomBalance() {
			return customBalance;
		}

		public void setCustomBalance(int customBalance) {
			this.customBalance = customBalance;
		}

		public Date getRefundTime() {
			return refundTime;
		}

		public void setRefundTime(Date refundTime) {
			this.refundTime = refundTime;
		}

		public String getAccessToken() {
			return accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}

		private String customType;
		
		private String customUserOpenId;
		
		private int customBalance;
		
		private Date refundTime;
		
		private String accessToken;
	}
	
	@Value("${refund.password}")
	private String refundPassword;
	
	@Autowired
	private CustomRepository customRepository;
	
	@Autowired
	private ManagerRepository managerRepository;
	
	@Autowired
	private RefundCustomRecordRepository refundCustomRecordRepository;
	
	@Autowired
	private PayCustomRecordRepository payCustomRecordRepository;
	
	@Autowired
	private CommonUtil commonUtil;
	
	@Autowired
	private WechatDataRepository wechatDataRepository;
	
	private CustomApi convertCustom(Custom custom)
	{
		CustomApi customApi = new CustomApi();
		customApi.setBalance(custom.getBalance());
		customApi.setId(custom.getId());
		customApi.setLimitPrice(custom.getLimitPrice());
		customApi.setNickname(custom.getNickname());
		customApi.setReserveDay(custom.getReserveDay());
		customApi.setType(custom.getType());
		
		return customApi;
	}
	
	private CardApi convertToCardApi(Custom custom) {
		CardApi cardApi = new CardApi();
		cardApi.setBalance(custom.getBalance());
		cardApi.setId(custom.getNickname());
		
		return cardApi;
	}
}
