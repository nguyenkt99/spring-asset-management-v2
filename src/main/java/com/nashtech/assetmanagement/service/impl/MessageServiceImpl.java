package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.MessageDTO;
import com.nashtech.assetmanagement.entity.ConversationEntity;
import com.nashtech.assetmanagement.entity.MessageEntity;
import com.nashtech.assetmanagement.entity.UserDetailEntity;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.ConversationRepository;
import com.nashtech.assetmanagement.repository.MessageRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    MessageRepository messageRepository;

    @Autowired
    ConversationRepository conversationRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public MessageDTO saveMessage(MessageDTO messageDTO) {
        ConversationEntity conversation;
        UserDetailEntity sender;
        MessageEntity message = messageDTO.toEntity();
        if(messageDTO.getConversationId() == null && !messageDTO.getTo().isEmpty()) { // new conversation
            conversation = new ConversationEntity();
            sender = userRepository.findByUserName(messageDTO.getSender())
                    .orElseThrow(() -> new ResourceNotFoundException("Sender not found!")).getUserDetail();
            UserDetailEntity to = userRepository.findByUserName(messageDTO.getTo())
                    .orElseThrow(() -> new ResourceNotFoundException("Receiver not found!")).getUserDetail();
            conversation.setUser1(sender);
            conversation.setUser2(to);
        } else {
            conversation = conversationRepository.findById(messageDTO.getConversationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found!"));
            sender = userRepository.findByUserName(messageDTO.getSender())
                    .orElseThrow(() -> new ResourceNotFoundException("Sender not found!")).getUserDetail();
        }

        conversation.setIsSeen(false);
        message.setConversation(conversation);
        message.setSender(sender);
        conversationRepository.save(conversation);
        return new MessageDTO(messageRepository.save(message));
    }
}
