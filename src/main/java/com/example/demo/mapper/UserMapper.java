package com.example.demo.mapper;

import com.example.demo.entities.UserInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user_info WHERE id = #{id}")
    UserInfo selectUser(Integer id);

    @Insert("INSERT INTO user_info (user_id, username, password, email, address, sex, sign, photo) VALUES (#{userId},#{username},#{password},#{email},#{address},#{sex},#{sign},#{photo})")
    int insertUser(UserInfo userInfo);

    @Select("SELECT * FROM user_info WHERE user_id = #{userId}")
    List<UserInfo> findByUserId(String userId);

    @Update("update user set user_id=#{userId},username=#{username},password=#{password}, email=#{email},address=#{address},sex=#{sex},sign=#{sign},photo=#{photo} where id=#{id}")
    int updateUser(UserInfo userInfo);

}
