package com.spriwind.Controller;


import com.spriwind.Entity.OwnedCoin;
import com.spriwind.Service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.spriwind.Entity.User;
import com.spriwind.Entity.Result;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is to create API about User actions to accept requests sent by web.
 */
@CrossOrigin(origins = "http://localhost:8081")
@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userservice;

    // The API of finding user with id.
    @GetMapping("/getuser")
    public Result getUser(@RequestParam Integer id) {
        User user = userservice.getUser(id);
        return Result.success(user);
    }

    // The API of editing user's data.
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
        log.info("id: " + user.toString());
        userservice.change(user);
        return Result.success("update successfully");
    }

    // The API of editing user's password.
    @PostMapping("/savepassword")
    public Result savePassword(@RequestBody User user) {
        log.info("id: " + user.toString());
        userservice.changePassword(user);
        return Result.success("update successfully");
    }

    // The API of returning coin data.
    @GetMapping("/getcoin")
    public Result getCoin(@RequestParam String name) {
        log.info("name" + name);
        List<OwnedCoin> oc = userservice.getCoin(name);
        return Result.success(oc);
    }

    // The API of creating coin asset.
    @PostMapping("/createcoin")
    public Result createCoin(@RequestBody OwnedCoin coin, @RequestParam int number) {
        log.info("asset" + coin.toString());
        userservice.createCoin(coin, number);
        return Result.success("create successfully");
    }

    @PostMapping("/savecharts")
    public Result saveChartsPerference(@RequestParam String username, @RequestParam int userid, @RequestParam String coinname, @RequestParam String market, @RequestParam String charts) {
        log.info("chart" + charts + "username" + userid + "coinname" + coinname + "market" + market);
        userservice.saveChartsPerference(username, userid, coinname,market,charts);
        return Result.success("create successfully");
    }

    @GetMapping("/getcharts")
    public Result getCharts(@RequestParam Integer id) {
        log.info("id" + id);
        return Result.success(userservice.getPreferences(id));
    }

}
