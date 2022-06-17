package com.lishuo.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常捕获
 * AOP
 */
@RestControllerAdvice(annotations = {RestController.class, Controller.class})//拦截加入此注解的类
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 异常处理
     * 处理该类的异常
     * (唯一字段重复问题)
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.info(exception.getMessage());

        if(exception.getMessage().contains(("Duplicate entry"))){
            String[] split = exception.getMessage().split(" ");//按‘ ’分隔
            String msg = split[2]+"已存在";
            return R.error(msg);
        }

        return R.error("未知错误！");
    }
    /**
     * 异常处理
     * 处理该类的异常
     * (唯一字段重复问题)
     * @return
     */
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){
        log.info(exception.getMessage());
        return R.error(exception.getMessage());
    }

}
