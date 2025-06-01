package com.dysjsjy;

import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.dysjsjy.model.entity.Message;
import com.dysjsjy.model.enums.MessageEnum;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Message message = JSON.parseObject(msg, Message.class);

        if (message == null) {
            ctx.writeAndFlush("Invalid message format");
            return;
        }

        MessageEnum messageEnum = message.getType();

        if (messageEnum == null) {
            ctx.writeAndFlush("Invalid message type");
            return;
        }

        switch (messageEnum) {
            case LOGIN:
                handleLogin(ctx, message);
                break;
            case CREATE_ROOM:
                handleCreateRoom(ctx, message);
                break;
            case JOIN_ROOM:
                handleJoinRoom(ctx, message);
                break;
            case SEND_MESSAGE:
                handleSendMessage(message);
                break;
            case LOGOUT:
                handleLogout(ctx, message);
                break;
            default:
                ctx.writeAndFlush("Unknown message type");
                break;
        }
    }

    private void handleLogin(ChannelHandlerContext ctx, Message message) {
        UserManager.addUser(message.getUserId(), ctx.channel());
        ctx.writeAndFlush("Login successful: " + message.getUserId());
    }

    private void handleCreateRoom(ChannelHandlerContext ctx, Message message) {
        ChatRoomManager.createRoom(message.getRoomId(), message.getUserId());
        ctx.writeAndFlush("Room created: " + message.getRoomId());
    }

    private void handleJoinRoom(ChannelHandlerContext ctx, Message message) {
        ChatRoomManager.joinRoom(message.getRoomId(), message.getUserId());
        ctx.writeAndFlush("Joined room: " + message.getRoomId());
    }

    // todo 这里的消息会缺少用户Id
    private void handleSendMessage(Message message) {
        Set<String> users = ChatRoomManager.getRoomUsers(message.getRoomId());
        for (String userId : users) {
            Channel channel = UserManager.getUserChannel(userId);
            if (channel != null) {
                channel.writeAndFlush(JSON.toJSONString(message));
            }
        }
    }

    private void handleLogout(ChannelHandlerContext ctx, Message message) {
        UserManager.removeUser(message.getUserId());
        ChatRoomManager.getRoomUsers(message.getRoomId())
                .forEach(room -> ChatRoomManager.leaveRoom(room, message.getUserId()));
        ctx.writeAndFlush("Logout successful");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 客户端断开连接时清理
        UserManager.removeUser(getUserIdByChannel(ctx.channel()));
    }

    private String getUserIdByChannel(Channel channel) {
        return UserManager.getOnlineUsers().entrySet().stream()
                .filter(entry -> entry.getValue() == channel)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }

}
