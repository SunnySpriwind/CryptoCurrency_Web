package com.spriwind.Mapper;

import com.spriwind.Entity.OwnedCoin;
import com.spriwind.Entity.Preference;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import com.spriwind.Entity.User;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @author Qimeng Chen
 * @since 2024/07/01
 * This class is to connect with DataBase and return data to UserService layer.
 */
@Mapper
public interface UserMapper {

    @Select("select * from Users where id = #{id}")
    User findUserById(Integer id);

    @Update("UPDATE Users SET name = #{name}, email = #{email} WHERE id = #{id}")
    void change(User user);

    @Update("UPDATE Users SET password = #{password} WHERE id = #{id}")
    void changePassword(User user);

    @Select("select * from Users where name = #{name}")
    User findByUsername(String username);

    @Select("select * from Users where name = #{name} and password = #{password}")
    User getByUsernameAndPassword(User user);

    @Insert("insert into Users(name, password, email) values (#{name}, #{password}, #{email})")
    void save(User user);

    @Select("select * from OwnedCoin where owner = #{name}")
    List<OwnedCoin> getCoin(String name);

    @Select("select coinid from OwnedCoin")
    List<String> getCoinId();

    @Insert("insert into OwnedCoin(coinid, coin, owner) values(#{id}, #{coin}, #{owner})")
    void insertCoin(String id, String coin, String owner);

    @Insert("insert into UserPreferences(username,userid,currencyname,marketname,selectcharts) values (#{username},#{userid}, #{coinname},#{market},#{charts})")
    void saveCharts(String username,  int userid, String coinname, String market,  String charts);

    @Select("select * from UserPreferences WHERE userid = #{id}")
    List<Preference> getUserPreferences(int id);
}
