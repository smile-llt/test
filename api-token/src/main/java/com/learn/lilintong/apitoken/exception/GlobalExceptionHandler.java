package com.learn.lilintong.apitoken.exception;




import com.learn.lilintong.apitoken.common.CustomErrorCode;
import com.learn.lilintong.apitoken.model.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 全局异常处理起器
 */
@Slf4j
@Component
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 请求参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class
            , MissingServletRequestParameterException.class,})
    public ApiResult methodArgumentNotValidException(Exception e, HttpServletRequest req) {
        log.error("请求"+ req.getRequestURI() + "异常：" +e.getMessage(), e);

        //参数缺失异常
        if (e instanceof MissingServletRequestParameterException) {
            MissingServletRequestParameterException exception = (MissingServletRequestParameterException) e;
            String message = exception.getParameterName() + "不能为空";
            return error(CustomErrorCode.VALIDATE_ERROR.getCode(), message);
        }

        List<ObjectError> allErrors = new ArrayList<>();
        if (e instanceof BindException) {
            allErrors = ((BindException) e).getBindingResult().getAllErrors();
        } else if (e instanceof MethodArgumentNotValidException) {
            allErrors = ((MethodArgumentNotValidException) e).getBindingResult().getAllErrors();
        }
        StringBuffer errors = new StringBuffer();
        for (ObjectError allError : allErrors) {
            errors.append(allError.getDefaultMessage());
            break;
        }
        return error(CustomErrorCode.VALIDATE_ERROR.getCode(), errors.toString());
    }

    /**
     * 其他异常处理
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception ex) {
        ApiResult apiResult = new ApiResult();
        apiResult.setMsg(ex.getMessage());
        apiResult.setCode(CustomErrorCode.UNKNOWN_ERROR.getCode());
        log.error(HikLog.toLog(" error"), ex);
        return apiResult;
    }


    private <T> ApiResult<T> error(String code, String msg) {
        ApiResult apiResult = new ApiResult<T>();
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        return apiResult;
    }

}


