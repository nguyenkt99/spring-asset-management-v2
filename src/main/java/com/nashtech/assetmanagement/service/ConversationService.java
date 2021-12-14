package com.nashtech.assetmanagement.service;


import com.nashtech.assetmanagement.dto.ConversationDTO;
import com.nashtech.assetmanagement.dto.MessageDTO;

import java.util.List;

public interface ConversationService {
    List<ConversationDTO> getConversations(String username);
    void seenConversation(Long id);
    List<MessageDTO> getMessages(Long conversationId);
//    ConversationDTO createConversation(String receiver, String name);
}
