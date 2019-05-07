package com.example.demo.chatserver.data;

public class RegisterHead {
    private String msgType;
    private String userId;
    private String msgId;



    public RegisterHead(){

    }
    public RegisterHead(String msgType, String userId, String msgId) {
        this.msgType = msgType;
        this.userId = userId;
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}
