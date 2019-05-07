package com.example.demo.chatserver.handler;

import com.example.demo.chatserver.data.RegisterHead;
import com.example.demo.chatserver.session.SessionCache;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class HeartbeatServerHandler extends ChannelInboundHandlerAdapter {

    private final static InternalLogger log = InternalLoggerFactory.getInstance(HeartbeatServerHandler.class);
    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("HB\r\n",
            CharsetUtil.UTF_8));
    private Gson gson = new Gson();
    private static final String REGISTER = "register";
    private RegisterHead registerHead;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("客户端"+channel+"与pushServer建立长连接！");
        SessionCache.getInstance().saveIpChannel((SocketChannel) ctx.channel());

        ctx.fireChannelActive();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        SessionCache.getInstance().removeIpChannel((SocketChannel) ctx.channel());
        cause.printStackTrace();
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("客户端" + channel + "与服务器连接发生异常，异常原因：" + cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ctx.close();
        SocketChannel channel = (SocketChannel) ctx.channel();
        log.info("客户端" + channel + "与pushServer的长连接断开！");
        SessionCache.getInstance().removeIpChannel((SocketChannel) ctx.channel());
        if (registerHead != null){
            SessionCache.getInstance().removeUuidIpChannel(registerHead.getUserId(), (SocketChannel) ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        String message = msg.toString();
        if ("HB".equals(message)) {
            ctx.channel().writeAndFlush(HEARTBEAT_SEQUENCE.duplicate());
        } else {
            String result = analyzeData(message, ctx);
            log.info("pushServer给客户端的回复:----" + result);
            if (result != null && !"".equals(result.trim())) {
                ByteBuf RESULT = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(result + "\r\n", CharsetUtil.UTF_8));
                ctx.channel().writeAndFlush(RESULT.duplicate());

            }
        }
        ReferenceCountUtil.release(msg);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private String analyzeData(String text, ChannelHandlerContext ctx) {
        String result = "";
        try {
            JSONObject request = null;
            try {
                request = new JSONObject(text);
            } catch (JSONException e) {
                log.error("============>Data analyze error:: " + text);
                e.printStackTrace();
            }

            if (request != null) {
                registerHead = gson.fromJson(request.optString("head"), RegisterHead.class);

                SessionCache.getInstance().saveUserList(registerHead.getUserId());
                String msgType = registerHead.getMsgType();

                if (REGISTER.equals(msgType)) {
                    result = register(registerHead, ctx);

                } else {
                    result = sendMessageToTarget(request);
                }
            }
        } catch (JSONException e) {
            log.error("============>Data analyze error:: " + text);
            e.printStackTrace();
        }
        return result;
    }
    private ArrayList<String> msgList = new ArrayList<String>();
    private String sendMessageToTarget(JSONObject request){
        String toUserId = request.getJSONObject("head").getString("toUser");
        JSONObject reposedClient = new JSONObject();


        if (SessionCache.getInstance().userIdIPport.containsKey(toUserId)){
            List<String> ipPorts = SessionCache.getInstance().userIdIPport.get(toUserId);
            String ipPort = null;
            for (String ipPost : ipPorts){
                ipPort = ipPost;
            }

            if (SessionCache.getInstance().sessions.containsKey(ipPort)){
                JSONObject reposed = new JSONObject();
                JSONObject requestHead = request.getJSONObject("head");
                JSONObject head = new JSONObject();
                head.put("msgType", "send")
                        .put("fromUserId", requestHead.getString("forUser"))
                        .put("myUserId", requestHead.getString("toUser"));
                reposed.put("head", head)
                        .put("body", request.getJSONObject("body"));
                SocketChannel session = SessionCache.getInstance().sessions.get(ipPort);
                session.writeAndFlush(Unpooled.copiedBuffer(reposed.toString() + "\r\n", CharsetUtil.UTF_8).duplicate());
                reposedClient.put("msgType", "send")
                        .put("code", "1")
                        .put("content","发送成功");
                return reposedClient.toString();
            }
        }else {
            if (SessionCache.getInstance().userList.contains(toUserId)){
                msgList.add(request.toString());
                SessionCache.getInstance().saveSendMsgList(toUserId, msgList);

                reposedClient.put("msgType", "send")
                        .put("code", "1")
                        .put("content","发送成功");
                return reposedClient.toString();
            }
        }

        reposedClient.put("msgType", "send")
                        .put("code", "0")
                        .put("content", "发送失败");
        return reposedClient.toString();
    }

    private String register(RegisterHead registerHead, ChannelHandlerContext ctx) {



        String code;
        String info;
        JSONObject requestBody = new JSONObject();
        try {
            String hostAddress = ctx.channel().remoteAddress().toString();

            dataSaved(registerHead.getUserId(),hostAddress);

            code = "0";
            info = "注册执行成功";
            requestBody.put("code", code);
            requestBody.put("info", info);

            if (SessionCache.getInstance().sendMsgList.containsKey(registerHead.getUserId())){
                for (String msg : SessionCache.getInstance().sendMsgList.get(registerHead.getUserId())){
                    ctx.channel().writeAndFlush(Unpooled.copiedBuffer(msg + "\r\n", CharsetUtil.UTF_8).duplicate());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return createResponse(registerHead, requestBody);
    }

    private void dataSaved(String userId, String hostAddress) {
        SessionCache.getInstance().saveUserIdIpChannel(userId,hostAddress);
    }

    private String createResponse(RegisterHead registerHead, JSONObject requestBody) {
        JSONObject response = new JSONObject();
        JSONObject head = new JSONObject();
        try {
            head.put("msgType", registerHead.getMsgType());
            head.put("msgId", registerHead.getMsgId());
            response.put("head", head);
            response.put("body", requestBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
