package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.chat.MessageModel;
import com.nashtech.assetmanagement.dto.NotificationDTO;

import java.util.List;

public interface FirebaseService {
    NotificationDTO saveNotification(NotificationDTO notificationDTO);
    List<NotificationDTO> getNotifications();
    void saveMessage(MessageModel message);
    List<MessageModel> getMessages(String sender, String receiver);
}