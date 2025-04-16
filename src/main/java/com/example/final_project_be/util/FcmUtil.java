package com.example.final_project_be.util;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FcmUtil {

    public void sendMulticast(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("FCM 토큰이 비어 있음. 알림을 전송하지 않음.");
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .addAllTokens(tokens)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);
            log.info("총 {}건 중 {}건 성공", response.getResponses().size(), response.getSuccessCount());


            response.getResponses().stream()
                    .filter(r -> !r.isSuccessful())
                    .forEach(r -> log.warn("실패: {}", r.getException().getMessage()));

        } catch (FirebaseMessagingException e) {
            log.error("다중 FCM 전송 중 오류 발생", e);
        }
    }
}
