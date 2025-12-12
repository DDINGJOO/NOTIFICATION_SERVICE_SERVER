package com.teambind.springproject.adapter.in.rest.sms;

import com.teambind.springproject.adapter.in.rest.sms.dto.SendSmsRequest;
import com.teambind.springproject.adapter.in.rest.sms.dto.SendSmsResponse;
import com.teambind.springproject.application.port.in.SendSmsUseCase;
import com.teambind.springproject.application.port.in.SendSmsUseCase.SmsTarget;
import jakarta.validation.Valid;
import java.util.List;
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
        log.debug("SMS 발송 요청. type={}, targetCount={}", request.getType(), request.getTargets().size());

        List<SmsTarget> targets = request.getTargets().stream()
                .map(t -> new SmsTarget(t.getUserId(), t.getPhoneNumber()))
                .toList();

        List<Long> notificationIds = sendSmsUseCase.sendSms(
                request.getType(),
                targets,
                request.getContent()
        );

        return ResponseEntity.ok(SendSmsResponse.success(notificationIds));
    }
}
