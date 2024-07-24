package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Sunny Spriwind
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private Integer code; // status code (1, success; 0, error)
    private String message; // message
    private Object data; // data

    public static Result success() {
        return new Result(1, "success", null);
    }

    public static Result success(Object data) {
        return new Result(1, "success", data);
    }

    public static Result error(String message){
        return new Result(0, message, null);
    }
}
