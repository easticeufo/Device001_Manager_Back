package com.madongfang.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.madongfang.api.BillRecordApi;
import com.madongfang.api.ChargeRecordApi;
import com.madongfang.api.CustomApi;
import com.madongfang.api.CustomPageApi;
import com.madongfang.api.PaymentRecordApi;
import com.madongfang.api.RefundApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Manager;
import com.madongfang.service.CustomRecordService;
import com.madongfang.service.CustomService;
import com.madongfang.util.WechatUtil;
import com.madongfang.util.WechatUtil.TempleteMessage4;

@RestController
@RequestMapping(value="/api/customs")
public class CustomController {

	@GetMapping(params="page")
	public CustomPageApi getCustoms(@RequestParam(required=false) String nickname, 
			@PageableDefault(size=30) Pageable pageable)
	{
		return customService.getCustoms(nickname, pageable);
	}
	
	@GetMapping(value="/{customId}")
	public CustomApi getCustom(@PathVariable int customId)
	{
		return customService.getCustom(customId);
	}
	
	@PostMapping(value="/{customId}/refund")
	public ReturnApi refund(HttpServletRequest request, @SessionAttribute Manager manager, 
			@PathVariable int customId, @RequestBody RefundApi refundApi)
	{
		String contextPath = request.getContextPath();
		int index = contextPath.indexOf("_Manager");
		if (index != -1)
		{
			contextPath = contextPath.substring(0, index);
		}
		String basePath = request.getScheme() + "://" + request.getServerName() + 
				":" + request.getServerPort() + contextPath;
		logger.debug("basePath={}", basePath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		CustomService.RefundResult refundResult = customService.refund(manager.getId(), customId, refundApi);
		if ("W".equals(refundResult.getCustomType())) // 微信用户需要推送退款消息
		{
			TempleteMessage4 refundMessage = new TempleteMessage4();
			refundMessage.getFirst().setValue("你的退款已到账");
			refundMessage.getKeyword1().setValue(refundApi.getReason());
			refundMessage.getKeyword2().setValue(sdf.format(refundResult.getRefundTime()));
			refundMessage.getKeyword3().setValue(String.format("%.2f元", (float)refundApi.getAmount() / 100));
			refundMessage.getKeyword4().setValue(String.format("%.2f元", (float)refundResult.getCustomBalance() / 100));
			refundMessage.getRemark().setValue("退款到账户中的钱可以在下次充电时使用");
			try {
				wechatUtil.sendTempleteMessage(refundResult.getCustomUserOpenId(), 
						wechatTemplateIdRefund, basePath + "/api/login?menu=mine", 
						refundMessage, refundResult.getAccessToken());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("catch Exception:", e);
			}
		}
		
		return new ReturnApi(0, "OK");
	}
	
	@GetMapping(value="/{customId}/records/bill", params="page")
	public Page<BillRecordApi> getBillRecords(@PathVariable int customId, 
			@PageableDefault(size=30, sort={"id"}, direction=Direction.DESC) Pageable pageable)
	{
		return customRecordService.getBillRecords(customId, pageable);
	}
	
	@GetMapping(value="/{customId}/records/payment", params="page")
	public Page<PaymentRecordApi> getPaymentRecords(@PathVariable int customId, 
			@PageableDefault(size=30, sort={"time"}, direction=Direction.DESC) Pageable pageable) 
	{
		return customRecordService.getPaymentRecords(customId, pageable);
	}
	
	@GetMapping(value="/{customId}/records/charge", params="page")
	public Page<ChargeRecordApi> getChargeRecords(@PathVariable int customId, 
			@PageableDefault(size=30, sort={"time"}, direction=Direction.DESC) Pageable pageable) 
	{
		return customRecordService.getChargeRecords(customId, pageable);
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${wechat.templateId.refund}")
	private String wechatTemplateIdRefund;
	
	@Autowired
	private WechatUtil wechatUtil;
	
	@Autowired
	private CustomService customService;
	
	@Autowired
	private CustomRecordService customRecordService;
}
