package com.spriwind.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author Sunny Spriwind
 * @since
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoinGeckoKlineResponse {
    private LocalDateTime timestamp;
    private double open;
    private double high;
    private double low;
    private double close;
}
