package com.nashtech.assetmanagement.chat;

import com.nashtech.assetmanagement.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private FirebaseService firebaseService;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, MessageModel message, Authentication authentication) {
        System.out.println();
        System.out.println("handling send message: " + message + " to: " + to);
        message.setTime(new Date());
        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
        firebaseService.saveMessage(message);
    }

}

