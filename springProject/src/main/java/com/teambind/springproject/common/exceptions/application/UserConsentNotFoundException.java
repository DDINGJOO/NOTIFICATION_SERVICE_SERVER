package com.teambind.springproject.common.exceptions.application;

import com.teambind.springproject.common.exceptions.CustomException;
import com.teambind.springproject.common.exceptions.ErrorCode;

/**
 * 사용자 동의 정보를 찾을 수 없을 때 발생하는 예외
 * HTTP 404 Not Found
 */
public class UserConsentNotFoundException extends CustomException {
	
	public UserConsentNotFoundException() {
		super(ErrorCode.USER_CONSENT_NOT_FOUND);
	}
	
	public UserConsentNotFoundException(String userId) {
		super(ErrorCode.USER_CONSENT_NOT_FOUND, "User consent not found: " + userId);
	}
	
	@Override
	public String getExceptionType() {
		return "APPLICATION";
	}
}
