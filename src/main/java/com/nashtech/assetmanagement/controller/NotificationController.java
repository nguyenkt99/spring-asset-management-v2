package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.NotificationDTO;
import com.nashtech.assetmanagement.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    NotificationService notificationService;

    @GetMapping
    List<NotificationDTO>  getNotifications() throws ExecutionException, InterruptedException {
        return notificationService.getNotifications();
    }

}
