package com.sitescape.ef.exception;

/**
 * @author Jong Kim
 *
 */
public abstract class CheckedCodedException extends CheckedException implements ErrorCodeSupport {
    private String errorCode;
    private Object[] errorArgs;
    
    public CheckedCodedException(String errorCode, Object[] errorArgs) {
        super();
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public CheckedCodedException(String errorCode, Object[] errorArgs, String message) {
        super(message);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public CheckedCodedException(String errorCode, Object[] errorArgs, String message, Throwable cause) {
        super(message, cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }
    public CheckedCodedException(String errorCode, Object[] errorArgs, Throwable cause) {
        super(cause);
        setErrorCode(errorCode);
        setErrorArgs(errorArgs);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getErrorArgs() {
        return errorArgs;
    }

    private void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    private void setErrorArgs(Object[] errorArgs) {
        this.errorArgs = errorArgs;
    }
}
