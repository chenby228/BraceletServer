package com.example.demo.services;

import com.example.demo.entities.UserInfo;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    public UserInfo selectUser(int id) {
        return userMapper.selectUser(id);
    }

    public int insertUser(UserInfo userInfo){
        return userMapper.insertUser(userInfo);
    }

    public List<UserInfo> findByUserId(String userId){
        return userMapper.findByUserId(userId);
    }
}
