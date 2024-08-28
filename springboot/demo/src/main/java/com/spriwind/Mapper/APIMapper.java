package com.spriwind.Mapper;

import com.spriwind.Entity.Coin;
import com.spriwind.Entity.CoinGeckoKlineResponse;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is to connect DataBase and insert data.
 */

@Mapper
public interface APIMapper {

    @Insert("INSERT INTO CoinGecko (name, price, market, pricetime, vol, marketcap) VALUES (#{name}, #{price}, #{market}, #{pricetime}, #{vol}, #{marketcap})")
    void insertCoin(Coin coin);

    @Insert("INSERT INTO CoinMarketCap (name, price, market, pricetime, vol,marketcap) VALUES (#{name}, #{price}, #{market}, #{pricetime}, #{vol},#{marketcap})")
    void insertCoinTwo(Coin coin);

    @Insert("INSERT INTO CoinGecko_Kline (name, pricetime, open, high, low, close, granularity) " +
            "VALUES (#{coinId}, #{kline.timestamp}, #{kline.open}, #{kline.high}, #{kline.low}, #{kline.close},#{granularity})")
    void insertKlineData(String coinId, CoinGeckoKlineResponse kline, String granularity);

    @Delete("DELETE FROM CoinGecko_Kline WHERE pricetime < NOW();")
    void deleteKline();
}
