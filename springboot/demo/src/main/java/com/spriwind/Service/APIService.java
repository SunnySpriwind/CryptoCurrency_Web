package com.spriwind.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spriwind.Entity.Coin;
import com.spriwind.Entity.CoinGeckoKlineResponse;
import com.spriwind.Entity.CoinGeckoResponse;
import com.spriwind.Entity.CoinMarketCapResponse;
import com.spriwind.Mapper.APIMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is to call the third APIs.
 */

@Slf4j
@Service
public class APIService {

    @Autowired
    private APIMapper apimapper;

    @PostConstruct
    public void callAPIatStart() throws InterruptedException {
        // The same API only is allowed to call at 5 different times.
        apimapper.deleteKline();
        fetchAndInsertCoinData("bitcoin", 1);
        fetchAndInsertCoinData("ethereum", 1);
        fetchAndInsertCoinData("binancecoin", 1);
        // Because there is a limit of calling API in one min.
        // To avoid exceeding of calling API at callAPI method, let thread sleep for 1 min.
        Thread.sleep(80000);
        fetchAndInsertCoinData("bitcoin", 7);
        fetchAndInsertCoinData("ethereum", 7);
        fetchAndInsertCoinData("binancecoin", 7);
        Thread.sleep(80000);
        fetchAndInsertCoinData("bitcoin", 30);
        fetchAndInsertCoinData("ethereum", 30);
        fetchAndInsertCoinData("binancecoin", 30);
        Thread.sleep(80000);
    }

    // Call API every 120 seconds
    @Scheduled(fixedRate = 120000)
    public void callAPI() {
        // Call CoinGecko API
        try {
            fetchAndInsertCoinData("bitcoin", "Bitcoin");
        } catch (Exception e) {
            System.err.println("Failed to fetch and insert data for Bitcoin: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            fetchAndInsertCoinData("ethereum", "Ethereum");
        } catch (Exception e) {
            System.err.println("Failed to fetch and insert data for Ethereum: " + e.getMessage());
            e.printStackTrace();
        }

        try {
            fetchAndInsertCoinData("binancecoin", "BinanceCoin");
        } catch (Exception e) {
            System.err.println("Failed to fetch and insert data for BinanceCoin: " + e.getMessage());
            e.printStackTrace();
        }
        // Call CoinMarketCap API
        try {
            fetchAndInsertCoinDataTwo("BTC,ETH,BNB", "BTC,ETH,BNB");
        } catch (Exception e) {
            System.err.println("Failed to fetch and insert data for CoinMarketCap: " + e.getMessage());
            e.printStackTrace();
        }

    }

    // Call API
    public void fetchAndInsertCoinData(String ids, String coinName) {
        log.info("Scheduled task started for {}", coinName);

        //API URL
        String apiUrl = String.format("https://api.coingecko.com/api/v3/simple/price?ids=%s&vs_currencies=usd&include_market_cap=true&include_24hr_vol=true&include_last_updated_at=true", ids);

        // Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("accept", "application/json")
                .build();

        // Parse JSON
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("API Response JSON: " + response.body());

            if (response.statusCode() == 200 && response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, CoinGeckoResponse> responseMap = objectMapper.readValue(response.body(), new TypeReference<Map<String, CoinGeckoResponse>>() {
                });

                // Find JSON with coinName
                CoinGeckoResponse coinData = responseMap.get(ids);

                // Lowercase the coin name
                if (coinData != null) {
                    Coin coin = new Coin();
                    if (coinName == null || coinName.isEmpty()) {
                        log.info("No Name Error");
                    }
                    coinName = coinName.substring(0, coinName.length()).toLowerCase();

                    // Load into entity class
                    // Name
                    coin.setName(coinName);
                    coin.setPrice(coinData.getUsd().toString());
                    coin.setMarket("1");

                    // Time
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime truncatedNow = now.withSecond(0).withNano(0);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = truncatedNow.format(formatter);
                    LocalDateTime pricetime = LocalDateTime.parse(formattedDateTime, formatter);
                    coin.setPricetime(pricetime);

                    // Vol
                    coin.setVol(coinData.getUsd24hVol().toString());

                    //marketcap
                    coin.setMarketcap(coinData.getUsdMarketcap().toString());

                    log.info("Inserting coin data: {}", coin);

                    // Insert data
                    apimapper.insertCoin(coin);
                }
            } else {
                log.error("Failed to fetch data from CoinGecko API. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching data from CoinGecko API firsttime", e);

            // If sending requests is not successful, send again.
            try {
                log.info("Retrying request...");
                HttpResponse<String> retryResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                log.info("Retry API Response JSON: " + retryResponse.body());

                if (retryResponse.statusCode() == 200 && retryResponse.body() != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, CoinGeckoResponse> responseMap = objectMapper.readValue(retryResponse.body(), new TypeReference<Map<String, CoinGeckoResponse>>() {
                    });

                    // Find JSON with coinName
                    CoinGeckoResponse coinData = responseMap.get(ids);

                    if (coinData != null) {
                        Coin coin = new Coin();
                        if (coinName == null || coinName.isEmpty()) {
                            log.info("No Name Error");
                        }
                        coinName = coinName.substring(0, coinName.length()).toLowerCase();

                        // Load into entity class
                        // Name
                        coin.setName(coinName);
                        coin.setPrice(coinData.getUsd().toString());
                        coin.setMarket("1");

                        // Time
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime truncatedNow = now.withSecond(0).withNano(0);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = truncatedNow.format(formatter);
                        LocalDateTime pricetime = LocalDateTime.parse(formattedDateTime, formatter);
                        coin.setPricetime(pricetime);

                        // Vol
                        coin.setVol(coinData.getUsd24hVol().toString());

                        // Marketcap
                        coin.setMarketcap(coinData.getUsdMarketcap().toString());

                        log.info("Inserting coin data after retry: {}", coin);

                        // Insert data
                        apimapper.insertCoin(coin);
                    }
                } else {
                    log.error("Retry failed to fetch data from CoinGecko API. Status code: " + retryResponse.statusCode());
                }
            } catch (Exception retryException) {
                log.error("Retry failed", retryException);
            }
        }

    }

    public void fetchAndInsertCoinDataTwo(String ids, String coinName) {
        log.info("Scheduled task started for CoinMarketCap data for {}", coinName);

        //API URL
        String apiUrl = String.format("https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol=%s&convert=USD", ids);

        // Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("accept", "application/json")
                .header("X-CMC_PRO_API_KEY", "b13448ca-2c50-4c4a-91ee-d3c1e18dd314")
                .build();

        // Parse JSON
        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("API Response JSON: " + response.body());

            if (response.statusCode() == 200 && response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                CoinMarketCapResponse coinMarketCapResponse = objectMapper.readValue(response.body(), CoinMarketCapResponse.class);

                Map<String, CoinMarketCapResponse.CoinMarketCapData> dataMap = coinMarketCapResponse.getData();

                for (Map.Entry<String, CoinMarketCapResponse.CoinMarketCapData> entry : dataMap.entrySet()) {
                    String symbol = entry.getKey();
                    CoinMarketCapResponse.CoinMarketCapData coinData = entry.getValue();

                    if (coinData != null) {
                        CoinMarketCapResponse.CoinMarketCapData.Quote.USD coinUSDData = coinData.getQuote().getUSD();

                        // Create Coin object and set values
                        Coin coin = new Coin();

                        // Name
                        if (symbol.equals("BNB")) {
                            coin.setName("binancecoin");
                        } else if (symbol.equals("ETH")) {
                            coin.setName("ethereum");
                        } else if (symbol.equals("BTC")) {
                            coin.setName("bitcoin");
                        } else {
                            coin.setName(symbol.toLowerCase());
                        }

                        // Price
                        coin.setPrice(coinUSDData.getPrice().toString());

                        // Market
                        coin.setMarket("3");

                        // Time
                        LocalDateTime now = LocalDateTime.now();
                        LocalDateTime truncatedNow = now.withSecond(0).withNano(0);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDateTime = truncatedNow.format(formatter);
                        LocalDateTime pricetime = LocalDateTime.parse(formattedDateTime, formatter);
                        coin.setPricetime(pricetime);

                        // Vol
                        coin.setVol(coinUSDData.getVolume24h().toString());

                        //Marketcap
                        coin.setMarketcap(coinUSDData.getMarketcap().toString());

                        log.info("Inserting coin data: {}", coin);

                        // Insert data
                        apimapper.insertCoinTwo(coin);
                    }
                }
            } else {
                log.error("Failed to fetch data from CoinMarketCap API. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching data from CoinMarketCap API", e);
        }
    }

    public void fetchAndInsertCoinData(String coinId, int day) {
        log.info("Fetching data for {} with day = {}", coinId, day);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss");

        // API URL
        String apiUrl = String.format("https://api.coingecko.com/api/v3/coins/%s/ohlc?vs_currency=usd&days=%d", coinId, day);

        // Request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("accept", "application/json")
                .header("Authorization", "CG-Waexam7mmJjENrTLCej9PKKd")
                .build();

        try {
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            log.info("API Response JSON: " + response.body());

            if (response.statusCode() == 200 && response.body() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                List<List<Double>> responseList = objectMapper.readValue(response.body(), new TypeReference<List<List<Double>>>() {
                });

                for (List<Double> ohlc : responseList) {
                    CoinGeckoKlineResponse kline = new CoinGeckoKlineResponse();

                    // Setting the timestamp as LocalDateTime and formatting
                    long timestamp = ohlc.get(0).longValue();
                    LocalDateTime pricetime = LocalDateTime.ofEpochSecond(timestamp / 1000, 0, ZoneOffset.UTC);
                    String formattedPricetime = pricetime.format(formatter);
                    LocalDateTime finalPricetime = LocalDateTime.parse(formattedPricetime, formatter);
                    kline.setTimestamp(finalPricetime);

                    // Setting OHLC values
                    kline.setOpen(ohlc.get(1));
                    kline.setHigh(ohlc.get(2));
                    kline.setLow(ohlc.get(3));
                    kline.setClose(ohlc.get(4));

                    String granularity;
                    if (day <= 2) {
                        granularity = "30min";
                    } else if (day < 30) {
                        granularity = "4h";
                    } else {
                        granularity = "4d";
                    }

                    // Insert data into the database
                    apimapper.insertKlineData(coinId, kline, granularity);

                    log.info("Inserted coin kline data: {}", kline);
                }
            } else {
                log.error("Failed to fetch data from CoinGecko API. Status code: " + response.statusCode());
            }
        } catch (Exception e) {
            log.error("Error occurred while fetching data from CoinGecko API", e);
        }
    }

}




