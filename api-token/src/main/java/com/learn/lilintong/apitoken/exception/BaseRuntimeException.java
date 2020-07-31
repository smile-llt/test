package com.learn.lilintong.apitoken.exception;

import com.learn.lilintong.apitoken.common.IErrorCode;
import com.learn.lilintong.apitoken.util.StringFormatUtils;

/**
 * @author lilintong
 * @create 2020/7/22
 */
public class BaseRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String code;
    private Object[] params;

    public BaseRuntimeException(String message) {
        super(message);
    }

    public BaseRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseRuntimeException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BaseRuntimeException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public BaseRuntimeException(String code, String message, Object... params) {
        super(StringFormatUtils.getExceptionMessage(message, params));
        this.code = code;
        this.params = params;
    }

    public BaseRuntimeException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BaseRuntimeException(IErrorCode errorCode, Object[] args) {
        super(StringFormatUtils.getExceptionMessage(errorCode.getMessage(), args));
        this.code = errorCode.getCode();
        if (null != args) {
            this.params = (Object[])args.clone();
        }

    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object[] getParams() {
        return null == this.params ? null : (Object[])this.params.clone();
    }

    public void setParams(Object[] params) {
        if (null == params) {
            this.params = null;
        } else {
            this.params = (Object[])params.clone();
        }

    }
}
