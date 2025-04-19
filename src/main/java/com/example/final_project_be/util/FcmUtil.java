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
    
    /**
     * 단일 디바이스에 긴 메시지를 포함한 알림을 전송합니다.
     * 특히 트레이너에게 PT 일정 명단과 같은 긴 내용의 메시지를 전송할 때 사용합니다.
     * 
     * @param token FCM 토큰
     * @param title 알림 제목
     * @param body 알림 내용 (긴 메시지 가능)
     * @return 전송 성공 여부
     */
    public boolean sendToDevice(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            log.warn("FCM 토큰이 비어 있음. 알림을 전송하지 않음.");
            return false;
        }

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("전송 성공: token={} / response={}", token, response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("전송 실패: token={} / error={}", token, e.getMessage(), e);
            return false;
        }
    }
}
