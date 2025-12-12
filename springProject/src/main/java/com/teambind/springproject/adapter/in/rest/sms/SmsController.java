package com.teambind.springproject.adapter.in.rest.sms;

import com.teambind.springproject.adapter.in.rest.sms.dto.SendSmsRequest;
import com.teambind.springproject.adapter.in.rest.sms.dto.SendSmsResponse;
import com.teambind.springproject.application.port.in.SendSmsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SMS 발송 REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SendSmsUseCase sendSmsUseCase;

    /**
     * SMS 발송
     */
    @PostMapping("/send")
    public ResponseEntity<SendSmsResponse> sendSms(@Valid @RequestBody SendSmsRequest request) {
        log.debug("SMS 발송 요청. userId={}, phoneNumber={}", request.getUserId(), request.getPhoneNumber());

        Long notificationId = sendSmsUseCase.sendSms(
                request.getUserId(),
                request.getPhoneNumber(),
                request.getContent()
        );

        return ResponseEntity.ok(SendSmsResponse.success(notificationId));
    }
}
