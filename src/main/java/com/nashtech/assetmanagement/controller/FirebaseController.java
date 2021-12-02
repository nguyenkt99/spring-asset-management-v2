package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.chat.MessageModel;
import com.nashtech.assetmanagement.dto.NotificationDTO;
import com.nashtech.assetmanagement.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/firebase")
public class FirebaseController {
    @Autowired
    FirebaseService firebaseService;

    @GetMapping("/notifications")
    List<NotificationDTO>  getNotifications() {
        return firebaseService.getNotifications();
    }

    @GetMapping("/messages")
    List<MessageModel> getMessages(@RequestParam String sender, @RequestParam String receiver) {
        return firebaseService.getMessages(sender, receiver);
    }

}
