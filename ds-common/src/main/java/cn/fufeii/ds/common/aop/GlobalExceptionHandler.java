package cn.fufeii.ds.common.aop;

import cn.fufeii.ds.common.enumerate.ExceptionEnum;
import cn.fufeii.ds.common.exception.BizException;
import cn.fufeii.ds.common.model.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 *
 * @author FuFei
 * @date 2022/3/13
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_CLIENT_TPL = "客户端请求异常：{}";


    /**
     * 拦截不支持媒体类型
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpMediaTypeNotSupportedException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class
    })
    @ResponseStatus(HttpStatus.OK)
    public Object httpMediaTypeNotSupport(Exception exception) {
        String reason = exception.getMessage();
        log.warn(LOG_CLIENT_TPL, reason);
        return CommonResult.fail(ExceptionEnum.CLIENT_ERROR.getCode(), reason);
    }


    /**
     * 请求参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        String reason = "";
        if (fieldError != null) {
            reason = "字段[" + fieldError.getField() + "]：" + fieldError.getDefaultMessage();
        }
        log.warn(LOG_CLIENT_TPL, reason);
        return CommonResult.fail(ExceptionEnum.CLIENT_ERROR.getCode(), reason);
    }


    /**
     * 业务异常
     */
    @ExceptionHandler(BizException.class)
    @ResponseStatus(HttpStatus.OK)
    public Object bizError(BizException e) {
        log.warn("业务异常", e);
        return CommonResult.fail(e.getCode(), e.getMessage());
    }


    /**
     * 其他异常
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    public Object serverError(Throwable e) {
        log.warn("内部异常", e);
        return CommonResult.fail(ExceptionEnum.SERVER_ERROR.getCode(), "内部异常");
    }

}
