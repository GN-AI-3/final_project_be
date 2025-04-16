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

        for (String token : tokens) {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();


            try {
                String response = FirebaseMessaging.getInstance().send(message);
                log.info("전송 성공: token={} / response={}", token, response);
            } catch (FirebaseMessagingException e) {
                log.error("전송 실패: token={} / error={}", token, e.getMessage(), e);
            }
        }
    }

    // 단일 전송용 메서드도 있으면 유지
    public void sendPush(String token, String title, String body) {
        sendMulticast(List.of(token), title, body);
    }
}
