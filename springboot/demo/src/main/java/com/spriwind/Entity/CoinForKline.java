package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Sunny Spriwind
 * @since
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinForKline {

    private String name;


    private LocalDateTime pricetime;


    private BigDecimal open;


    private BigDecimal high;


    private BigDecimal low;


    private BigDecimal close;


    private String granularity;
}
