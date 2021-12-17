package com.nashtech.assetmanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "time")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name="sender")
    private UserDetailEntity sender;

    @ManyToOne
    @JoinColumn(name="conversation_id")
    private ConversationEntity conversation;
}
