package com.spriwind.Service;

import com.spriwind.Entity.OwnedCoin;
import com.spriwind.Entity.Preference;
import com.spriwind.Entity.User;
import com.spriwind.Exception.CustomException;
import com.spriwind.Mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Random;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class includes logic operations which is related to requests accepted at LoginController
 * and UserController.
 */

@Service
public class UserService {

    @Autowired
    private UserMapper usermapper;

    // Find user with id.
    public User getUser(Integer id) {
        return usermapper.findUserById(id);
    }

    // Edit user's data.
    public void change(User user) {
        usermapper.change(user);
    }

    // Change user's password.
    public void changePassword(User user) {
        usermapper.changePassword(user);
    }

    // Users login.
    public User login(User user){
        return usermapper.getByUsernameAndPassword(user);
    }

    // Users register
    public void register(User user){
        if(usermapper.findByUsername(user.getName())!= null){
            throw new CustomException("username is exist");
        }
        usermapper.save(user);
    }

    // Return user's assets.
    public List<OwnedCoin> getCoin(String name) {
        return usermapper.getCoin(name);
    }

    // Create new assets.
    public void createCoin(OwnedCoin coin, int number) {
        for (int i = 0; i < number; i++) {
            // To check the id of coin is repetitive.
            String id = null;
            if (coin.getCoinid() == null) {
                boolean loop = true;
                while (loop == true) {
                    List<String> allId = usermapper.getCoinId();
                    Random random = new Random();
                    int coinid = random.nextInt(10000 + 1);
                    id = "#" + coinid;
                    if (!allId.contains(id)) {
                        loop = false;
                    }
                }
            }
            usermapper.insertCoin(id,coin.getCoin(), coin.getOwner());
        }
    }

    public void saveChartsPerference(String username,  int userid, String coinname, String market,  String charts) {
        usermapper.saveCharts(username,userid,coinname,market,charts);
    }

    public List<Preference> getPreferences(int id) {
        return usermapper.getUserPreferences(id);
    }
}
