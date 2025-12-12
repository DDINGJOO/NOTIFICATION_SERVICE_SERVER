package com.teambind.springproject.adapter.in.rest.email;

import com.teambind.springproject.adapter.in.rest.email.dto.SendEmailRequest;
import com.teambind.springproject.adapter.in.rest.email.dto.SendEmailResponse;
import com.teambind.springproject.application.port.in.SendEmailUseCase;
import com.teambind.springproject.application.port.in.SendEmailUseCase.EmailTarget;
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
 * 이메일 발송 REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/email")
@RequiredArgsConstructor
public class EmailController {

    private final SendEmailUseCase sendEmailUseCase;

    /**
     * 이메일 발송
     */
    @PostMapping("/send")
    public ResponseEntity<SendEmailResponse> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        log.debug("이메일 발송 요청. type={}, targetCount={}", request.getType(), request.getTargets().size());

        List<EmailTarget> targets = request.getTargets().stream()
                .map(t -> new EmailTarget(t.getUserId(), t.getEmail()))
                .toList();

        List<Long> notificationIds = sendEmailUseCase.sendEmail(
                request.getType(),
                targets,
                request.getTitle(),
                request.getContent()
        );

        return ResponseEntity.ok(SendEmailResponse.success(notificationIds));
    }
}
