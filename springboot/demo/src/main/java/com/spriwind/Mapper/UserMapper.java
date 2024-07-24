package com.spriwind.Mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import com.spriwind.Entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Sunny Spriwind
 * @since
 */
@Mapper
public interface UserMapper {

    public List<User> findAll();

    @Select("select * from Users where name = #{name}")
    public User findByUsername(String username);

    @Select("select * from Users where name = #{name} and password = #{password}" )
    User getByUsernameAndPassword(User user);

    @Insert("insert into Users(name, password, email) values (#{name}, #{password}, #{email})")
    void save(User user);
}
