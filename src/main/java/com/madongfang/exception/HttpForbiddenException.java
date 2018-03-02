package com.madongfang.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.madongfang.api.ReturnApi;

@ResponseStatus(value=HttpStatus.FORBIDDEN)
public class HttpForbiddenException extends HttpException {

	public HttpForbiddenException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HttpForbiddenException(ReturnApi returnApi) {
		super(returnApi);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;

}
