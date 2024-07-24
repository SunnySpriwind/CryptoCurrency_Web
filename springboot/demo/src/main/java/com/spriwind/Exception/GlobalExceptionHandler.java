package com.spriwind.Exception;

import com.spriwind.Entity.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Sunny Spriwind
 * @since
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result exception(Exception e){
        e.printStackTrace();
        return Result.error("Wrong Operations");
    }

    @ExceptionHandler(CustomException.class)
    public Result customException(CustomException c){
        c.printStackTrace();
        return Result.error(c.getMessage());
    }
}
