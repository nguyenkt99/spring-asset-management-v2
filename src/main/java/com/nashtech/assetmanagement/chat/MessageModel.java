package com.nashtech.assetmanagement.chat;

import lombok.Data;

import java.util.Date;

@Data
public class MessageModel {
    private String sender;
    private String receiver;
    private String content;
    private Date time;
}
