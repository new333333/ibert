package com.sitescape.ef.exception;

import com.sitescape.team.util.NLT;

/**
 * @author Jong Kim
 *
 */
public abstract class UncheckedCodedException extends UncheckedException implements ErrorCodeSupport {
    private String errorCode;
    private Object[] errorArgs;
    
    public UncheckedCodedException(String errorCode) {
    	super();
    	setErrorCode(errorCode);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs) {
        super();
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs, String message) {
        super(message);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(message, cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public UncheckedCodedException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }

    public String getLocalizedMessage() {
    	try {
    		return NLT.get(getErrorCode(), getErrorArgs());
    	}
    	catch(Exception e) {
    		return super.getMessage();
    	}
    }
    
    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getErrorArgs() {
        return errorArgs;
    }

    public void setErrorArgs(Object[] errorArgs) {
        this.errorArgs = errorArgs;
    }

    private void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
