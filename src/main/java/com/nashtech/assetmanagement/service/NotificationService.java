package com.nashtech.assetmanagement.service;

import com.nashtech.assetmanagement.dto.CategoryDTO;
import com.nashtech.assetmanagement.dto.NotificationDTO;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface NotificationService {
    NotificationDTO send(NotificationDTO notificationDTO) throws ExecutionException, InterruptedException;
    List<NotificationDTO> getNotifications() throws ExecutionException, InterruptedException;
}
