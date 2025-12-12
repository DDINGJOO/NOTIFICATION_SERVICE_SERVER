package com.teambind.springproject.adapter.in.rest.push;

import com.teambind.springproject.adapter.in.rest.push.dto.SendPushRequest;
import com.teambind.springproject.adapter.in.rest.push.dto.SendPushResponse;
import com.teambind.springproject.application.port.in.SendPushUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 푸시 알림 REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
public class PushController {

    private final SendPushUseCase sendPushUseCase;

    /**
     * 푸시 알림 발송
     */
    @PostMapping("/send")
    public ResponseEntity<SendPushResponse> sendPush(@Valid @RequestBody SendPushRequest request) {
        log.debug("푸시 알림 발송 요청. userId={}, deviceToken={}", request.getUserId(), request.getDeviceToken());

        Long notificationId = sendPushUseCase.sendPush(
                request.getUserId(),
                request.getDeviceToken(),
                request.getTitle(),
                request.getBody(),
                request.getData()
        );

        return ResponseEntity.ok(SendPushResponse.success(notificationId));
    }
}
