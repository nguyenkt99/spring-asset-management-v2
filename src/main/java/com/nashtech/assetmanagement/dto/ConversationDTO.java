package com.nashtech.assetmanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nashtech.assetmanagement.entity.ConversationEntity;
import com.nashtech.assetmanagement.entity.MessageEntity;
import com.nashtech.assetmanagement.entity.UserDetailEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDTO {
    private Long id;
    private String username1;
    private String fullName1;
    private String username2;
    private String fullName2;
    private String lastMessage;
    private String sender;
    private LocalDateTime time;
    private Boolean isSeen;

    public ConversationDTO(ConversationEntity entity) {
        this.id = entity.getId();
        this.username1 = entity.getUser1().getUser().getUserName();
        this.fullName1 = entity.getUser1().getFirstName() + " " + entity.getUser1().getLastName();
        this.username2 = entity.getUser2().getUser().getUserName();
        this.fullName2 = entity.getUser2().getFirstName() + " " + entity.getUser2().getLastName();
        if(entity.getMessages().size() > 0) {
            MessageEntity message = entity.getMessages().stream().sorted(Comparator.comparingLong(MessageEntity::getId))
                    .collect(Collectors.toList())
                    .get(entity.getMessages().size() - 1);
            this.lastMessage = message.getContent();
            this.isSeen = entity.getIsSeen();
            this.sender = message.getSender().getUser().getUserName();
            this.time = message.getTime();
        }
    }

}
