package com.madongfang.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.madongfang.api.LoginApi;
import com.madongfang.api.ManagerRegisterApi;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Manager;
import com.madongfang.exception.HttpBadRequestException;
import com.madongfang.exception.HttpUnauthorizedException;
import com.madongfang.service.ManagerService;
import com.madongfang.util.WechatUtil;

@Controller
@RequestMapping(value="/api/login")
public class LoginController {

	@GetMapping
	public String oauthLogin(HttpServletRequest request, HttpServletResponse response, 
			@RequestParam(name="code", required=false)String code, 
			@RequestParam(name="state", required=false)String state,
			@RequestParam(name="menu", required=false)String menu,
			@RequestParam(name="card", required=false)String card,
			Model model) 
	{
		String basePath = request.getScheme()+"://"+request.getServerName()+request.getContextPath();
		logger.debug("basePath={}", basePath);
		
		if (card != null && card.length() == 10) // 将10位十进制内码(正码)转化为8位16进制的卡号
		{
			card = String.format("%08X", Long.valueOf(card));
		}
		
		if (code != null) // 微信OAuth登录后重定向处理
		{
			try {
				String loginResult = "您不是管理员，请先注册成为管理员！";
				
				WechatUtil.UserInfo userInfo = wechatUtil.getUserInfo(code);
				if (userInfo == null)
				{
					logger.error("获取微信用户信息失败");
					model.addAttribute("errorInfo", "获取微信用户信息失败");
					return "error";
				}
				
				Manager manager = null;
				String redirect = null;
				switch (state) {
				case "loginCode":
					String loginCode = managerService.getLoginCode(userInfo.getOpenid());
					if (loginCode != null)
					{
						loginResult = String.format("您的验证码是：%s，有效时间：1分钟", loginCode);
					}
					
					model.addAttribute("loginResult", loginResult);
					return "loginResult";

				case "devices":
				case "records":
				case "report":
					manager = managerService.oauthLogin(userInfo);
					if (manager == null)
					{
						model.addAttribute("loginResult", loginResult);
						return "loginResult";
					}
					request.getSession().setAttribute("manager", manager);
					redirect = basePath + "/index.html#?/main/" + state;
					logger.debug("redirect address:" + redirect);
					response.sendRedirect(redirect);
					return null; // 这里不能使用spring自带的"redirect:"进行重定向，spring的重定向功能会在url中加入session导致angularjs页面访问出错
					
				case "managerRegister":
					request.getSession().setAttribute("openId", userInfo.getOpenid());
					request.getSession().setAttribute("nickname", userInfo.getNickname());
					redirect = basePath + "/index.html#?/managerRegister";
					logger.debug("redirect address:" + redirect);
					response.sendRedirect(redirect);
					return null;
					
				default:
					if (state.startsWith("card"))
					{
						String cardId = state.substring("card".length());
						manager = managerService.oauthLogin(userInfo);
						if (manager == null || manager.getLevel() != 4)
						{
							model.addAttribute("loginResult", "您没有管理充电卡的权限");
							return "loginResult";
						}
						request.getSession().setAttribute("manager", manager);
						redirect = basePath + "/index.html#?/main/cards/" + cardId;
						logger.debug("redirect address:" + redirect);
						response.sendRedirect(redirect);
						return null;
					}
					else 
					{
						logger.warn("不支持的menu选项：state={}", state);
						
						model.addAttribute("errorInfo", "不支持的menu选项");
						return "error";
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error("catch Exception:", e);
				model.addAttribute("errorInfo", e.getMessage());
				return "error";
			}
		}
		else // 重定向到OAuth登录连接
		{
			try {
				if (menu != null) // 菜单登陆选项
				{
					state = menu;
				}
				else if (card != null) // 管理员添加卡或给卡充值
				{
					state = "card" + card;
				}
				else // 其他二维码形式
				{
					state = null;
				}
				
				String site = "redirect:";
				site += wechatUtil.oauth2Redirect(WechatUtil.SCOPE_USERINFO, basePath + "/api/login", state);

				return  site;
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				logger.error("catch Exception:", e);
				model.addAttribute("errorInfo", e.getMessage());
				return "error";
			}
		}
	}
	
	@PostMapping
	public @ResponseBody ReturnApi codeLogin(HttpSession session, @RequestBody LoginApi loginApi) 
	{
		ReturnApi returnApi = new ReturnApi(0, "OK");
		
		Manager manager = managerService.getManager(loginApi.getUsername());
		if (manager == null)
		{
			returnApi.setReturnCode(-1);
			returnApi.setReturnMsg("不存在的用户");
		}
		else if (manager.getLoginCode() == null || !manager.getLoginCode().equalsIgnoreCase(loginApi.getLoginCode()))
		{
			returnApi.setReturnCode(-2);
			returnApi.setReturnMsg("登陆码错误");
		}
		else if (manager.getGenerateTime() == null || (new Date().getTime() - manager.getGenerateTime().getTime()) > 60 * 1000)
		{
			returnApi.setReturnCode(-3);
			returnApi.setReturnMsg("登陆码超期");
		}
		else // 登陆成功
		{
			session.setAttribute("manager", manager);
		}
		
		if (returnApi.getReturnCode() < 0)
		{
			throw new HttpUnauthorizedException(returnApi);
		}
		else
		{
			return returnApi;
		}
	}
	
	@PostMapping(value="/managerRegister")
	public @ResponseBody ReturnApi managerRegister(HttpSession session, 
			@RequestBody ManagerRegisterApi managerRegisterApi)
	{
		String openId = (String)session.getAttribute("openId");
		String nickname = (String)session.getAttribute("nickname");
		if (openId == null || nickname == null)
		{
			throw new HttpUnauthorizedException(new ReturnApi(-1, "未登录，请先通过微信登录!"));
		}
		
		if (managerRegisterApi.getUsername() == null || managerRegisterApi.getInviteCode() == null)
		{
			throw new HttpBadRequestException(new ReturnApi(-2, "用户名和邀请码不能为空！"));
		}
		
		Manager manager = managerService.register(openId, nickname, managerRegisterApi.getUsername(), managerRegisterApi.getInviteCode());
		session.setAttribute("manager", manager);
		
		return new ReturnApi(0, "OK");
	}
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private WechatUtil wechatUtil;
	
	@Autowired
	private ManagerService managerService;
}
