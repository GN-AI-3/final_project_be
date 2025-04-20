package com.example.final_project_be.util;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FcmUtil {

    public void sendMulticast(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("FCM 토큰이 비어 있음. 알림을 전송하지 않음.");
            return;
        }

        for (String token : tokens) {
            sendPush(token, title, body);
        }
    }

    // 단일 전송용 메서드도 있으면 유지
    public void sendPush(String token, String title, String body) {
        if (token == null || token.isEmpty()) {
            log.warn("FCM 토큰이 비어 있음. 알림을 전송하지 않음.");
            return;
        }

        try {
            log.debug("sendPush 메서드에서 FCM 메시지 발송 시작: token={}, title={}", token, title);
            
            // 데이터 메시지 추가 (notification과 함께 전송)
            Map<String, String> data = new HashMap<>();
            data.put("title", title);
            data.put("body", body);
            data.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
            data.put("message-type", "pt_notification");  // 키 이름 수정 (message_type -> message-type)
            
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)  // 데이터 메시지 추가
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .putHeader("apns-priority", "10")
                            .setAps(Aps.builder()
                                    .setContentAvailable(true)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("sendPush: FCM 전송 성공: token={}, response={}", token, response);
        } catch (FirebaseMessagingException e) {
            log.error("sendPush: FCM 전송 실패: token={}, error={}, errorCode={}", token, e.getMessage(), e.getErrorCode(), e);
        } catch (Exception e) {
            log.error("sendPush: FCM 전송 중 예외 발생: token={}, error={}", token, e.getMessage(), e);
        }
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
            log.debug("sendToDevice: FCM 메시지 발송 시작: token={}, title={}, body={}", token, title, body);
            
            // 데이터 메시지 추가 (notification과 함께 전송)
            Map<String, String> data = new HashMap<>();
            data.put("title", title);
            data.put("body", body);
            data.put("click_action", "FLUTTER_NOTIFICATION_CLICK");
            data.put("message-type", "pt_summary");  // 키 이름 수정 (message_type -> message-type)
            
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putAllData(data)  // 데이터 메시지 추가
                    .setAndroidConfig(AndroidConfig.builder()
                            .setPriority(AndroidConfig.Priority.HIGH)
                            .build())
                    .setApnsConfig(ApnsConfig.builder()
                            .putHeader("apns-priority", "10")
                            .setAps(Aps.builder()
                                    .setContentAvailable(true)
                                    .setSound("default")
                                    .build())
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("sendToDevice: FCM 전송 성공: token={}, response={}", token, response);
            return true;
        } catch (FirebaseMessagingException e) {
            log.error("sendToDevice: FCM 전송 실패: token={}, error={}, errorCode={}", token, e.getMessage(), e.getErrorCode(), e);
            return false;
        } catch (Exception e) {
            log.error("sendToDevice: FCM 전송 중 예외 발생: token={}, error={}", token, e.getMessage(), e);
            return false;
        }
    }
}
