package com.example.demo.controllers;

import com.example.demo.entities.UserInfo;
import com.example.demo.services.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Product controller.
 */
@Controller
public class UserInfoController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userInfoService;

    @Autowired
    public UserInfoController(UserService userInfoService) {
        this.userInfoService = userInfoService;
    }


    @RequestMapping(value = "/request", method = RequestMethod.POST)
    @ResponseBody
    public String requestHandle(HttpServletRequest servletRequest){
        String request = getRequestParam(servletRequest);
        logger.info("接收到App的请求消息:" + request);
        JSONObject requestJson = new JSONObject(request);
        String msgType = requestJson.getString("msgType");
        String respond = msgTypeHandler(msgType, requestJson);
        logger.info("服务器返回给App的消息：" + respond);
        return respond;
    }
    private String getRequestParam(HttpServletRequest servletRequest) {
        String requestJson = "";
        try {
            ServletInputStream inputStream = servletRequest.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String temp = br.readLine();
            while (temp != null) {
                sb.append(temp);
                temp = br.readLine();
            }
            br.close();
            requestJson = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestJson;
    }

    private String msgTypeHandler(String msgType, JSONObject jsonObject){

        UserInfo userInfo = null;
        String flag = null;
        switch (msgType){
            case "register":
                List<UserInfo> userInfoList = userInfoService.findByUserId(jsonObject.getString("userId"));
                for (UserInfo info : userInfoList){
                    userInfo = info;
                }
                if (userInfo != null){
                    logger.info("用户已存在，重复注册");
                    flag = "{\"status\":\"1\"}";
                }else {
                    logger.info("不存在用户");
                    UserInfo toAppRespond = new UserInfo();
                    toAppRespond.setUserId(jsonObject.getString("userId"));
                    toAppRespond.setUsername(jsonObject.getString("username"));
                    toAppRespond.setPassword(jsonObject.getString("password"));

                    int i = userInfoService.insertUser(toAppRespond);
                    logger.info("注册成功返回：" + i);
                    flag = "{\"status\":\"0\"}";
                }
                break;
            case "login":
                List<UserInfo> loginList = userInfoService.findByUserId(jsonObject.getString("userId"));
                try {
                    userInfo = loginList.get(0);
                    if (userInfo.getPassword().equals(jsonObject.getString("password"))){
                        logger.info("登录成功" + userInfo);
                        flag = "{\"status\":\"1\"}";
                    }else {
                        logger.info("登录失败" + "，密码不正确");
                        flag = "{\"status\":\"0\"}";
                    }
                }catch (Exception e){
                    logger.info("不存在用户");
                    flag = "{\"status\":\"0\"}";
                }
                break;
            case "updateInfo":
                //次要功能待完善
                logger.info("修改个人信息，待开发！！");
                flag = "{\"status\":\"0\"}";
                break;

            default:
                logger.info("待开发！");
                return "null";
        }
        return flag;
    }
}
