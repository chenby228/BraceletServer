package com.example.demo.chatserver.data;

import java.util.List;

public class RequestRegisterBody {
    private List<String> sessionKey;
    private String userOpenId;

    public List<String> getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(List<String> sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getUserOpenId() {
        return userOpenId;
    }

    public void setUserOpenId(String userOpenId) {
        this.userOpenId = userOpenId;
    }
}
