package com.spriwind.Controller;


import com.spriwind.Service.UserService;
import com.spriwind.Utils.JWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.spriwind.Entity.User;
import com.spriwind.Entity.Result;

import java.util.HashMap;


/**
 * @author Sunny Spriwind
 * @since
 */

@CrossOrigin(origins = "http://localhost:8081")
@Slf4j
@RestController
public class LoginController {

    @Autowired
    private UserService userservice;

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        log.info("user login:", user);
        User u = userservice.login(user);

        if(u != null) {
            HashMap<String, Object> claims = new HashMap<>();
            claims.put("id", u.getId());
            claims.put("name", u.getName());

            String jwt = JWT.generateJWT(claims);

            return Result.success(jwt);
        }

        return u!=null?Result.success():Result.error("username or password error");
    }

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        log.info("user register:", user);
        userservice.register(user);
        return Result.success();
    }

}
