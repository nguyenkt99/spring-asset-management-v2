package com.nashtech.assetmanagement.chat;

import com.nashtech.assetmanagement.dto.MessageDTO;
import com.nashtech.assetmanagement.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class MessageController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    MessageService messageService;

    @MessageMapping("/chat/{to}")
    public void sendMessage(@DestinationVariable String to, MessageDTO message) {
//        System.out.println("handling send message: " + message + " to: " + to);
        message.setTime(LocalDateTime.now());

        // if new conversation then save conversation before send to get conversation id
        //
        boolean isNewConversation = false;
        if(message.getConversationId() == null)
            isNewConversation = true;

        if(isNewConversation == true) {
            message.setTo(to);
            message = messageService.saveMessage(message);
        }
        simpMessagingTemplate.convertAndSend("/topic/messages/" + to, message);
        simpMessagingTemplate.convertAndSend("/topic/messages/" + message.getSender(), message);
        if(isNewConversation == false)
            messageService.saveMessage(message);
    }
}

