package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByConversation_IdOrderById(Long id);
}
