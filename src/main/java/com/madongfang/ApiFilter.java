package com.madongfang;

import java.io.IOException;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.madongfang.api.ReturnApi;
import com.madongfang.entity.Manager;

/**
 * Servlet Filter implementation class ApiFilter
 */
@Component
public class ApiFilter implements Filter {

    /**
     * Default constructor. 
     */
    public ApiFilter() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// place your code here
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		
		httpResponse.setContentType("application/json");
		httpResponse.setCharacterEncoding("UTF-8");
		
		String uri = httpRequest.getRequestURI();
		String query = httpRequest.getQueryString();
		if (query != null)
		{
			logger.debug("ApiFilter: method=" + httpRequest.getMethod() + ", url=" + uri + "?" + query);
		}
		else
		{
			logger.debug("ApiFilter: method=" + httpRequest.getMethod() + ", url=" + uri);
		}
		
		/* 验证用户登陆 */
		int level = 99;
		boolean authenticated = false; // 是否已登录认证
		String auth = httpRequest.getHeader("Authorization");
		logger.debug("http auth={}", auth);
		String authString = username + ":" + password;
    	String authStringEnc = new String(Base64.getEncoder().encode(authString.getBytes()));
		HttpSession session = httpRequest.getSession(false);
		String apiString = uri.substring(httpRequest.getContextPath().length());
		if ("OPTIONS".equals(httpRequest.getMethod()) 
				|| apiString.startsWith("/api/login")
				|| apiString.startsWith("/api/test")
				|| apiString.equals("/api/logout")) // 不需要登陆验证的命令
		{
			authenticated = true;
		}
		else // 普通用户登录
		{
			if (session != null && session.getAttribute("manager") != null)
			{
				authenticated = true;
				level = ((Manager)session.getAttribute("manager")).getLevel();
			}
			
			if (auth != null && auth.equals("Basic " + authStringEnc))
			{
				authenticated = true;
				level = 1;
			}
		}
		
		if (!authenticated)
		{
			ReturnApi returnApi = new ReturnApi(-1, "未登陆，请先登陆！");
			httpResponse.setStatus(401);
			response.getWriter().write(new ObjectMapper().writeValueAsString(returnApi));
			return;
		}
		
		/* 权限等级限制 */
		if (level > 1)
		{
			if (apiString.startsWith("/api/managers") 
					|| apiString.startsWith("/api/records")
					|| apiString.startsWith("/api/customs")
					|| apiString.startsWith("/api/devices")
					|| apiString.startsWith("/api/finance"))
			{
				ReturnApi returnApi = new ReturnApi(-2, "权限不足！");
				httpResponse.setStatus(403);
				response.getWriter().write(new ObjectMapper().writeValueAsString(returnApi));
				return;
			}
		}
		if (level > 2)
		{
			if ("PUT".equals(httpRequest.getMethod()))
			{
				ReturnApi returnApi = new ReturnApi(-2, "权限不足！");
				httpResponse.setStatus(403);
				response.getWriter().write(new ObjectMapper().writeValueAsString(returnApi));
				return;
			}
		}
		if (level != 4)
		{
			if (("POST".equals(httpRequest.getMethod()) && apiString.equals("/api/cards")) 
					|| (apiString.startsWith("/api/cards") && apiString.endsWith("/recharge")))
			{
				httpResponse.setStatus(403);
				response.getWriter().write(new ObjectMapper().writeValueAsString(new ReturnApi(-2, "权限不足！")));
				return;
			}
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${api.base64.username}")
	private String username;
	
	@Value("${api.base64.password}")
	private String password;
}
