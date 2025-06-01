package com.dysjsjy.model.entity;

import com.dysjsjy.model.enums.MessageEnum;

import lombok.Data;

/*
{
  "type": "SEND_MESSAGE",
  "userId": "user123",
  "roomId": "room456",
  "content": "Hello, everyone!",
  "datetime": "2024-09-11 10:30:00"
}
*/

@Data
public class Message {
    private MessageEnum type;
    private String userId;
    private String roomId;
    private String content;
    private String datetime;

}
