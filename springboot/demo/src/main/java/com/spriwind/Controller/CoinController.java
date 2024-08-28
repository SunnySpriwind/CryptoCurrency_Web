package com.spriwind.Controller;

import com.spriwind.Entity.Coin;
import com.spriwind.Entity.CoinForKline;
import com.spriwind.Entity.Result;
import com.spriwind.Service.CoinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is to create API about Coin data to accept requests sent by web.
 */

@CrossOrigin(origins = "http://localhost:8081")
@Slf4j
@RestController
public class CoinController {

    @Autowired
    private CoinService coinservice;

    // The API of real-time price without market parametric.
    @GetMapping("/realtime")
    public Result realTime(@RequestParam String name) {
        log.info("Name: " + name);
        List<Coin> c = coinservice.realtime(name);
        return Result.success(c);
    }

    // The API of Real-time price with market parametric.
    @GetMapping("/realtime/general")
    public Result realTimeWithMarket(@RequestParam String name, @RequestParam String market) {
        log.info("API:Realtime(Market) is starting, Paraname: " + name + ", Paramarket: " + market);
        List<Coin> c = coinservice.realTimeWithMarket(name, market);
        return Result.success(c);
    }

    // The API of Historical price.
    @GetMapping("/historicaldata")
    public Result historicalData(@RequestParam String name, @RequestParam String market,
                                 @RequestParam long startTime, @RequestParam long endTime) {
        log.info("API:History is starting, Paraname: " + name + ", Paramarket: " + market + ", ParastartTime: " + startTime + ", ParaendTime: " + endTime);
        List<Coin> c = coinservice.historicalData(name, market, startTime, endTime);
        return Result.success(c);
    }

    // The API of max price.
    @GetMapping("/realtime/maxprice")
    public Result maxPrice(@RequestParam String name, @RequestParam String market) {
        log.info("API:MaxPrice is starting, ParaName: " + name + ", Paramarket: " + market);
        Coin c = coinservice.maxPrice(name, market);
        return Result.success(c);
    }

    // The API of min price.
    @GetMapping("/realtime/minprice")
    public Result minPrice(@RequestParam String name, @RequestParam String market) {
        log.info("API:MinPrice is starting, ParaName: " + name + ", Paramarket: " + market);
        Coin c = coinservice.minPrice(name, market);
        return Result.success(c);
    }

    // The API of volumn.
    @GetMapping("/realtime/data")
    public Result getData(@RequestParam String name, @RequestParam String market) {
        log.info("API:Data is starting, ParaName: " + name + ", Paramarket: " + market);
        Coin c = coinservice.getData(name, market);
        return Result.success(c);
    }

    // The API of max volumn.
    @GetMapping("/realtime/maxvol")
    public Result maxVol(@RequestParam String name, @RequestParam String market) {
        log.info("API:MaxVol is starting, ParaName: " + name + ", Paramarket: " + market);
        Coin c = coinservice.maxVol(name, market);
        return Result.success(c);
    }

    // The API of min volumn.
    @GetMapping("/realtime/minvol")
    public Result minVol(@RequestParam String name, @RequestParam String market) {
        log.info("API:MinVol is starting, ParaName: " + name + ", Paramarket: " + market);
        Coin c = coinservice.minVol(name, market);
        return Result.success(c);
    }


    @GetMapping("/kline")
    public Result kline(@RequestParam String name, @RequestParam String period) {
        log.info("API:Kline is starting, ParaName: " + name);
        List<CoinForKline> chart = coinservice.drawKline(name, period);
        return Result.success(chart);
    }

    @GetMapping("/update")
    public Result update(@RequestParam String name, @RequestParam String market) {
        log.info("API:Update is starting, ParaName: " + name + ", Paramarket: " + market);
        Coin c = coinservice.update(name, market);
        return Result.success(c);
    }

}
