package com.nashtech.assetmanagement.repository;

import com.nashtech.assetmanagement.entity.ConversationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ConversationRepository extends JpaRepository<ConversationEntity, Long> {
    @Query("SELECT c FROM ConversationEntity c WHERE c.user1.user.userName = ?1 OR c.user2.user.userName = ?1")
    List<ConversationEntity> findAllByUsername(String username);


}
