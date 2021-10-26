package com.nashtech.assetmanagement.service.impl;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.nashtech.assetmanagement.chat.MessageModel;
import com.nashtech.assetmanagement.dto.NotificationDTO;
import com.nashtech.assetmanagement.exception.BadRequestException;
import com.nashtech.assetmanagement.security.services.UserDetailsImpl;
import com.nashtech.assetmanagement.service.FirebaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FirebaseServiceImpl implements FirebaseService {
    @Value("${app.firebase-config}")
    private String firebaseConfig;
    private FirebaseApp firebaseApp;

    @PostConstruct
    private void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfig).getInputStream())).build();

            if (FirebaseApp.getApps().isEmpty()) {
                this.firebaseApp = FirebaseApp.initializeApp(options);
            } else {
                this.firebaseApp = FirebaseApp.getInstance();
            }
        } catch (IOException e) {
            log.error("Create FirebaseApp Error", e);
        }
    }

    @Override
    public NotificationDTO saveNotification(NotificationDTO notificationDTO) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        String id = firestore.collection("notifications").document().getId();
        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("notifications")
                .document(id).set(notificationDTO);
        return notificationDTO;
    }

    @Override
    public List<NotificationDTO> getNotifications() {
        String username = null;
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();
        String role = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList()).get(0);
        if (role.equals("ROLE_STAFF")) {
            username = userDetails.getUsername();
        } else if (role.equals("ROLE_ADMIN")) {
            username = null;
        } else {
            throw new BadRequestException("You must be login to request!");
        }

        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("notifications").whereEqualTo("username", username).get();
        List<QueryDocumentSnapshot> documents = null;
        try {
            documents = future.get().getDocuments();
        } catch (Exception e) {
            log.error("Get documents error!");
        }

        List<NotificationDTO> notificationDTOs = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            notificationDTOs.add(document.toObject(NotificationDTO.class));
        }
        return notificationDTOs;
    }

    @Override
    public void saveMessage(MessageModel message) {
        Firestore firestore = FirestoreClient.getFirestore();
        String id = firestore.collection("messages").document().getId();
        ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("messages")
                .document(id).set(message);
    }

    @Override
    public List<MessageModel> getMessages(String sender, String receiver) {
        Firestore firestore = FirestoreClient.getFirestore();
        CollectionReference messages = firestore.collection("messages");
        ApiFuture<QuerySnapshot> future1 = messages.whereEqualTo("sender", "nguyen").whereEqualTo("receiver", "viet").get();
        ApiFuture<QuerySnapshot> future2 = messages.whereEqualTo("sender", "viet").whereEqualTo("receiver", "nguyen").get();

        List<QueryDocumentSnapshot> document1s = null;
        List<QueryDocumentSnapshot> document2s = null;
        try {
            document1s = future1.get().getDocuments();
            document2s = future2.get().getDocuments();
        } catch (Exception e) {
            log.error("Get documents error!");
        }

        List<MessageModel> messageList = new ArrayList<>();
        for (QueryDocumentSnapshot document : document1s)
            messageList.add(document.toObject(MessageModel.class));
        for (QueryDocumentSnapshot document : document2s)
            messageList.add(document.toObject(MessageModel.class));

        List<MessageModel> sortedList = messageList.stream().sorted(Comparator.comparing(MessageModel::getTime))
                .collect(Collectors.toList());

        return sortedList;
    }
}
