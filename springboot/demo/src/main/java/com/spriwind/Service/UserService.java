package com.spriwind.Service;

import com.spriwind.Entity.User;
import com.spriwind.Exception.CustomException;
import com.spriwind.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Sunny Spriwind
 * @since
 */

@Service
public class UserService {

    @Autowired
    private UserMapper usermapper;

    public List<User> getUser() {
        return usermapper.findAll();
    }

    public User login(User user){
        return usermapper.getByUsernameAndPassword(user);
    }

    public void register(User user){
        if(usermapper.findByUsername(user.getName())!= null){
            throw new CustomException("username is exist");
        }
        usermapper.save(user);
    }
}
