package com.dysjsjy.model.enums;

public enum MessageEnum {
    LOGIN,
    CREATE_ROOM,
    JOIN_ROOM,
    LEAVE_ROOM,
    SEND_MESSAGE,
    LOGOUT;

    public static MessageEnum getMessageEnum(String message) {
        for (MessageEnum messageEnum : MessageEnum.values()) {
            if (messageEnum.name().equals(message)) {
                return messageEnum;
            }
        }
        
        return null;
    }
}
