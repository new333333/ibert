package com.sitescape.team.util;

import com.sitescape.team.exception.CheckedCodedException;

public class XSSCheckException extends CheckedCodedException {

	private static final long serialVersionUID = 1L;
	
	private static final String XSSCheckException_ErrorCode = "errorcode.xss.check";

	public XSSCheckException() {
		super(XSSCheckException_ErrorCode);
	}
}
