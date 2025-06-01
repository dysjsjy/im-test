package com.dysjsjy;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {
    private static final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();

    public static void createRoom(String roomId, String userId) {
        rooms.computeIfAbsent(roomId, k -> new HashSet<>()).add(userId);
    }

    public static void joinRoom(String roomId, String userId) {
        rooms.computeIfAbsent(roomId, k -> new HashSet<>()).add(userId);
    }

    public static void leaveRoom(String roomId, String userId) {
        Set<String> users = rooms.get(roomId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty())
                rooms.remove(roomId);
        }
    }

    public static Set<String> getRoomUsers(String roomId) {
        return rooms.getOrDefault(roomId, new HashSet<>());
    }
}
