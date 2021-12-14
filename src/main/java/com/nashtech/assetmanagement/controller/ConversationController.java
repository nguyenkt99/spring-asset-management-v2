package com.nashtech.assetmanagement.controller;

import com.nashtech.assetmanagement.dto.ConversationDTO;
import com.nashtech.assetmanagement.dto.MessageDTO;
import com.nashtech.assetmanagement.dto.UserDTO;
import com.nashtech.assetmanagement.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    @Autowired
    ConversationService conversationService;

//    @PostMapping
//    public ConversationDTO createConversation(@RequestBody UserDTO userDTO, Authentication authentication) {
//        return conversationService.createConversation(userDTO.getUsername(), authentication.getName());
//    }

    @GetMapping
    public List<ConversationDTO> getConversations(Authentication authentication) {
        return conversationService.getConversations(authentication.getName());
    }

    @PostMapping("/{id}/seen")
    public void seenConversation(@PathVariable Long id) {
        conversationService.seenConversation(id);
    }

    @RequestMapping("/{conversationId}")
    public List<MessageDTO> getMessages(@PathVariable Long conversationId) {
        return conversationService.getMessages(conversationId);
    }
}
