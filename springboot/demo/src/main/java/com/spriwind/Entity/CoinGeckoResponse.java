package com.spriwind.Entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is an entity class of API response.
 */
public class CoinGeckoResponse {
    private BigDecimal usd;
    private BigDecimal usd24hVol;
    private BigDecimal usdmarketcap;
    private long lastUpdatedAt;

    // Getters and setters
    @JsonProperty("usd")
    public BigDecimal getUsd() {
        return usd;
    }

    @JsonProperty("usd")
    public void setUsd(BigDecimal usd) {
        this.usd = usd;
    }

    @JsonProperty("usd_24h_vol")
    public BigDecimal getUsd24hVol() {
        return usd24hVol;
    }

    @JsonProperty("usd_24h_vol")
    public void setUsd24hVol(BigDecimal usd24hVol) {
        this.usd24hVol = usd24hVol;
    }

    @JsonProperty("last_updated_at")
    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    @JsonProperty("last_updated_at")
    public void setLastUpdatedAt(long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @JsonProperty("usd_market_cap")
    public BigDecimal getUsdMarketcap() {
        return usdmarketcap;
    }

    @JsonProperty("usd_market_cap")
    public void setUsdMarketcap(BigDecimal usdMarketcap) {
        this.usdmarketcap = usdMarketcap;
    }
}
