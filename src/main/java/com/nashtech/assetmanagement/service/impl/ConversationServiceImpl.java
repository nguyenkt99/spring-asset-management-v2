package com.nashtech.assetmanagement.service.impl;

import com.nashtech.assetmanagement.dto.ConversationDTO;
import com.nashtech.assetmanagement.dto.MessageDTO;
import com.nashtech.assetmanagement.entity.ConversationEntity;
import com.nashtech.assetmanagement.entity.UserDetailEntity;
import com.nashtech.assetmanagement.exception.ResourceNotFoundException;
import com.nashtech.assetmanagement.repository.ConversationRepository;
import com.nashtech.assetmanagement.repository.MessageRepository;
import com.nashtech.assetmanagement.repository.UserRepository;
import com.nashtech.assetmanagement.service.ConversationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConversationServiceImpl implements ConversationService {
    @Autowired
    ConversationRepository conversationRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<ConversationDTO> getConversations(String username) {
        List<ConversationEntity> conversations = conversationRepository.findAllByUsername(username);
        List<ConversationDTO> conversationDTOs = new ArrayList<>();
        conversations.forEach((c) -> {
            if(c.getMessages().size() > 0) {
                conversationDTOs.add(new ConversationDTO(c));
            }
        });
        return conversationDTOs.stream()
                .sorted(Comparator.comparing(ConversationDTO::getTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void seenConversation(Long id) {
        ConversationEntity conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not exists!"));
        if(!conversation.getIsSeen()) {
            conversation.setIsSeen(true);
            conversationRepository.save(conversation);
        }
    }

    @Override
    public List<MessageDTO> getMessages(Long conversationId) {
        return messageRepository.findByConversation_IdOrderById(conversationId)
                .stream().map(MessageDTO::new).collect(Collectors.toList());
    }

//    @Override
//    public ConversationDTO createConversation(String receiver, String username) {
//        UserDetailEntity user1 = userRepository.findByUserName(username)
//                .orElseThrow(() -> new ResourceNotFoundException(username + " not found!")).getUserDetail();
//        UserDetailEntity user2 = userRepository.findByUserName(receiver)
//                .orElseThrow(() -> new ResourceNotFoundException(username + " not found!")).getUserDetail();
//        ConversationEntity conversation = new ConversationEntity();
//        conversation.setUser1(user1);
//        conversation.setUser2(user2);
//        return new ConversationDTO(conversationRepository.save(conversation));
//    }

}
