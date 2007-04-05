/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.sample;

import com.sitescape.team.exception.UncheckedCodedException;

public class SalaryMaxedOutException extends UncheckedCodedException {
	
	private static final String SalaryMaxedOutException_ErrorCode = "errorcode.salary.maxed.out";
	
	public SalaryMaxedOutException(String employeeName, int maxAmount) {
		super(SalaryMaxedOutException_ErrorCode, new Object[]{employeeName, new Integer(maxAmount)});
	}
}
