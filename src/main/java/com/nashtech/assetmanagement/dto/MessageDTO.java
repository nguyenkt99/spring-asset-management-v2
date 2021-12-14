package com.nashtech.assetmanagement.dto;

import com.nashtech.assetmanagement.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private String content;
    private String sender;
    private String to;
    private String fullName;
    private LocalDateTime time;
    private Long conversationId;

    public MessageDTO(MessageEntity message) {
        this.id = message.getId();
        this.content = message.getContent();
        this.time = message.getTime();
        this.sender = message.getSender().getUser().getUserName();
        this.fullName = message.getSender().getFirstName() + " " + message.getSender().getLastName();
        this.conversationId = message.getConversation().getId();
    }

    public MessageEntity toEntity() {
        MessageEntity entity = new MessageEntity();
        entity.setContent(this.content);
        entity.setTime(this.time);
        return entity;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", fullName='" + fullName + '\'' +
                ", time=" + time +
                ", conversationId=" + conversationId +
                '}';
    }
}
