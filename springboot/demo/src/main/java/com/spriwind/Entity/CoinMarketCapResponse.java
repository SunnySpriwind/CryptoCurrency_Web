package com.spriwind.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is an entity class of API response.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoinMarketCapResponse {
    private Status status;
    private Map<String, CoinMarketCapData> data;

    // Getters and setters
    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonProperty("data")
    public Map<String, CoinMarketCapData> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(Map<String, CoinMarketCapData> data) {
        this.data = data;
    }

    public static class Status {
        private String timestamp;
        private int error_code;
        private String error_message;
        private int elapsed;
        private int credit_count;
        private String notice;

        // Getters and setters
        @JsonProperty("timestamp")
        public String getTimestamp() {
            return timestamp;
        }

        @JsonProperty("timestamp")
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @JsonProperty("error_code")
        public int getErrorCode() {
            return error_code;
        }

        @JsonProperty("error_code")
        public void setErrorCode(int error_code) {
            this.error_code = error_code;
        }

        @JsonProperty("error_message")
        public String getErrorMessage() {
            return error_message;
        }

        @JsonProperty("error_message")
        public void setErrorMessage(String error_message) {
            this.error_message = error_message;
        }

        @JsonProperty("elapsed")
        public int getElapsed() {
            return elapsed;
        }

        @JsonProperty("elapsed")
        public void setElapsed(int elapsed) {
            this.elapsed = elapsed;
        }

        @JsonProperty("credit_count")
        public int getCreditCount() {
            return credit_count;
        }

        @JsonProperty("credit_count")
        public void setCreditCount(int credit_count) {
            this.credit_count = credit_count;
        }

        @JsonProperty("notice")
        public String getNotice() {
            return notice;
        }

        @JsonProperty("notice")
        public void setNotice(String notice) {
            this.notice = notice;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CoinMarketCapData {
        private String name;
        private Quote quote;

        // Getters and setters
        @JsonProperty("name")
        public String getName() {
            return name;
        }

        @JsonProperty("name")
        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("quote")
        public Quote getQuote() {
            return quote;
        }

        @JsonProperty("quote")
        public void setQuote(Quote quote) {
            this.quote = quote;
        }

        public static class Quote {
            private USD USD;

            // Getters and setters
            @JsonProperty("USD")
            public USD getUSD() {
                return USD;
            }

            @JsonProperty("USD")
            public void setUSD(USD USD) {
                this.USD = USD;
            }

            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class USD {
                private BigDecimal price;
                private BigDecimal volume24h;
                private BigDecimal marketcap;

                // Getters and setters
                @JsonProperty("price")
                public BigDecimal getPrice() {
                    return price;
                }

                @JsonProperty("price")
                public void setPrice(BigDecimal price) {
                    this.price = price;
                }

                @JsonProperty("volume_24h")
                public BigDecimal getVolume24h() {
                    return volume24h;
                }

                @JsonProperty("volume_24h")
                public void setVolume24h(BigDecimal volume24h) {
                    this.volume24h = volume24h;
                }

                @JsonProperty("market_cap")
                public BigDecimal getMarketcap() {
                    return marketcap;
                }

                @JsonProperty("market_cap")
                public void setMarketcap(BigDecimal marketcap) {
                    this.marketcap = marketcap;
                }
            }
        }
    }
}


