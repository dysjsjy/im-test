package com.dysjsjy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import lombok.Getter;

public class UserManager {

    @Getter
    private static final Map<String, Channel> onlineUsers = new ConcurrentHashMap<>();

    public static void addUser(String userId, Channel channel) {
        onlineUsers.put(userId, channel);
    }

    public static void removeUser(String userId) {
        onlineUsers.remove(userId);
    }

    public static Channel getUserChannel(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return onlineUsers.get(userId);
    }

}
