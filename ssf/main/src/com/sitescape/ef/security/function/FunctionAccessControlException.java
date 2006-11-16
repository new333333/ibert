package com.sitescape.ef.security.function;

import com.sitescape.ef.security.AccessControlException;

public class FunctionAccessControlException extends AccessControlException {
    
	private static final String FunctionAccessControlException_ErrorCode = "errorcode.function.denied";
	
	public FunctionAccessControlException(String username, String functionName, 
			Long workAreaId) {
		super(FunctionAccessControlException_ErrorCode, new Object[] {username, functionName, workAreaId});
	}
}
