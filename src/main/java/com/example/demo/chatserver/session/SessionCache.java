package com.example.demo.chatserver.session;

import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionCache {


    private final static Logger log = LoggerFactory.getLogger(SessionCache.class);
    private static SessionCache session = null;
    //sample: sessions(ip_port,socketchannel), 用于查找对应的channel,即App 的Tcp 连接
    public Map<String, SocketChannel> sessions = new HashMap<String, SocketChannel>();
    //sample: sessions(uuid,ip_port)
    public Map<String, List<String>> userIdIPport = new HashMap<String, List<String>>();
    //sample: 用户名
    public ArrayList<String> userList = new ArrayList<String>();

    //待发送消息列表
    public Map<String, List<String>> sendMsgList = new HashMap<String, List<String>>();
    public static SessionCache getInstance() {
        if (session == null) {
            session = new SessionCache();
        }
        return session;
    }

    public void saveUserList(String userId) {
        userList.add(userId);
    }

    public void saveSendMsgList(String userId, ArrayList<String> sendMsg){
        sendMsgList.put(userId, sendMsg);
    }

    private String getClientIpAndPort(SocketChannel session) {

        String ip = String.valueOf(session.remoteAddress().getAddress());
        String port = String.valueOf(session.remoteAddress().getPort());
        log.info(ip + ":" + port);
        return ip + ":" + port;
    }


    public boolean Channel4IpisExists(String key) {
        for (int i = 0; i < sessions.size(); i++) {
            log.info("ipPort: " + key + "channel: " + sessions.get(key));
        }
        return sessions.containsKey(key);
    }

    public boolean Ip4uuidisExists(String key) {
        for (int i = 0; i < userIdIPport.size(); i++) {
            for (int j = 0; j < userIdIPport.get(key).size(); j++) {
                log.info("uuid: " + key + "ipport: " + userIdIPport.get(key).get(j));
            }
        }
        return userIdIPport.containsKey(key);
    }

    public SocketChannel getIpChannel(String key) {
        return sessions.get(key);
    }


    public List<String> getUuidIpChannel(String key) {


        return userIdIPport.get(key);

    }

    /*HashMap:ClientIPAndKPort<--------->SocketChannel*/
    public void saveIpChannel(SocketChannel session) {
        String key = getClientIpAndPort(session);
        if (!sessions.containsKey(key)) {
            sessions.put(key, session);
            log.info("IpChannelHashMap 存储了IPChannel== " + sessions.size() + "Ip ==" + key + " session ==>" + session);
        }
    }

    public void removeIpChannel(SocketChannel session) {
        String key = getClientIpAndPort(session);
        if (sessions.containsKey(key)) {
            sessions.remove(key);
            log.info("IpChannelHashMap 删除了IPChannel== " + sessions.size() + "Ip ==" + key + " session ==>" + session);
        }
    }


    /*HashMap:UUID<--------->ClientIPAndPort*/
    public void saveUserIdIpChannel(String userId, String hostAddress) {
        if (!userIdIPport.containsKey(userId)) {
            ArrayList<String> list = new ArrayList<String>();
            list.add(hostAddress);
            userIdIPport.put(userId, list);
            log.info("UserIdIpChannelHashMap 存储了IPChannel== " + userIdIPport.size() + "userId ==" + userId + " ip_port ==>" + hostAddress);
        }else {
            userIdIPport.get(userId).add(hostAddress);
            for (int i = 0; i < userIdIPport.get(userId).size(); i++) {
                log.info("i muserId: " + userId + ", ip_port: " + userIdIPport.get(userId).get(i));
            }
        }
    }

    public void removeUuidIpChannel(String userId, SocketChannel session) {
        if (userIdIPport.containsKey(userId)) {
            userIdIPport.remove(userId);
            log.info("UserIdIpChannelHashMap 删除了IPChannel== " + userIdIPport.size() + "uuid ==" + userId + " ip_port ==>" + getClientIpAndPort(session));
        }
    }


}


