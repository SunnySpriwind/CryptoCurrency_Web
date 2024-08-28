package com.spriwind;

import com.spriwind.Entity.Coin;
import com.spriwind.Entity.User;
import com.spriwind.Mapper.CoinMapper;
import com.spriwind.Mapper.UserMapper;
import com.spriwind.Service.CoinService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private CoinService coinService;


    @Test
    public void test() {

    }
}
