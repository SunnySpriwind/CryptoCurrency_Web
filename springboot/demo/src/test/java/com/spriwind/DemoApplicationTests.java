package com.spriwind;

import com.spriwind.Entity.User;
import com.spriwind.Mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private UserMapper usermapper;

    @Test
    public void findAll() {
        List<User> users = usermapper.findAll();
        System.out.println(users);
    }

}
