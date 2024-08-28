package com.spriwind.Mapper;

import com.spriwind.Entity.Coin;
import com.spriwind.Entity.CoinForKline;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is to connect with DataBase and return data to CoinService layer.
 */

@Mapper
public interface CoinMapper {

    // Find data without market.
    @Select("SELECT price, pricetime,market FROM CoinGecko WHERE name = #{name}")
    List<Coin> findCoin(String name);

    // Find Coin data with market.
    List<Coin> findCoinWithMarket(String name, String market);

    // Find Coin data with time.
    List<Coin> findCoinWithTime(String name, String market, LocalDateTime start, LocalDateTime end);

    // Find Max price.
    List<Coin> findMaxPrice(String name, String market);

    // Find Min price.
    List<Coin> findMinPrice(String name, String market);

    // Find Max volumn.
    List<Coin> findMaxVol(String name, String market);

    // Find Min volumn.
    List<Coin> findMinVol(String name, String market);

    // Find current vol
    Coin getData(String name, String market);

    List<CoinForKline> kLineChart(String name, String period);

    Coin update(String name, String market);
}
