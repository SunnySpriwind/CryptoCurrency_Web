package com.spriwind.Exception;

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
public class CustomException extends RuntimeException {
    private String message;
}
