package com.nashtech.assetmanagement.dto;

import com.nashtech.assetmanagement.constants.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long idRequest;
    private NotificationType type;
    private String username;
    private String title;
    private Boolean isSeen;
    private Date createdDate;
}
