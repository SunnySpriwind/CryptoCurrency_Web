package com.spriwind.Service;

import com.spriwind.Entity.Coin;
import com.spriwind.Entity.CoinForKline;
import com.spriwind.Mapper.CoinMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;


/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class includes logic operations which is related to requests accepted at CoinController.
 */

@Slf4j
@Service
public class CoinService {

    @Autowired
    CoinMapper coinmapper;

    // Return real-time data without market.
    public List<Coin> realtime(String name) {
        return coinmapper.findCoin(name);
    }

    // Return real-time data with market.
    public List<Coin> realTimeWithMarket(String name, String market) {
        return coinmapper.findCoinWithMarket(name, market);
    }

    // Return historical data.
    public List<Coin> historicalData(String name, String market, long start, long end) {
        // To transform Unix to LocalDateTime.
        ZoneId zoneId = ZoneId.of("Europe/London");
        LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(start), zoneId);
        LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(end), zoneId);
        return coinmapper.findCoinWithTime(name, market, startDateTime, endDateTime);
    }


    // Return maxPrice
    public Coin maxPrice(String name, String market) {
        List<Coin> cs = coinmapper.findMaxPrice(name, market);
        Coin coin = cs.get(0);
        return coin;
    }

    // Return minPrice
    public Coin minPrice(String name, String market) {
        List<Coin> cs = coinmapper.findMinPrice(name, market);
        Coin coin = cs.get(0);
        return coin;
    }

    // Return maxVol
    public Coin maxVol(String name, String market) {
        List<Coin> cs = coinmapper.findMaxVol(name, market);
        Coin coin = cs.get(0);
        return coin;
    }

    // Return minVol
    public Coin minVol(String name, String market) {
        List<Coin> cs = coinmapper.findMinVol(name, market);
        Coin coin = cs.get(0);
        return coin;
    }

    // Return Vol
    public Coin getData(String name, String market) {
        Coin coin = coinmapper.getData(name, market);
        return coin;
    }

    public List<CoinForKline> drawKline(String name, String period) {
        List<CoinForKline> chart = coinmapper.kLineChart(name, period);
        return chart;
    }

    public Coin update(String name, String market) {
        Coin c = coinmapper.update(name, market);
        return c;
    }
}