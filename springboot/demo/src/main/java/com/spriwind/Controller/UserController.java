package com.spriwind.Controller;

import com.spriwind.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.spriwind.Entity.User;
import com.spriwind.Entity.Result;

import java.util.List;

/**
 * @author Sunny Spriwind
 * @since
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userservice;

    @GetMapping("/get")
    public List<User> getUser(){
        return userservice.getUser();
    }

}
