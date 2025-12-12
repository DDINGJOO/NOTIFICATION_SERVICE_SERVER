package com.teambind.springproject.adapter.in.rest.kakao;

import com.teambind.springproject.adapter.in.rest.kakao.dto.SendKakaoRequest;
import com.teambind.springproject.adapter.in.rest.kakao.dto.SendKakaoResponse;
import com.teambind.springproject.application.port.in.SendKakaoUseCase;
import com.teambind.springproject.application.port.in.SendKakaoUseCase.KakaoTarget;
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
 * 카카오 알림톡 REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kakao")
@RequiredArgsConstructor
public class KakaoController {

    private final SendKakaoUseCase sendKakaoUseCase;

    /**
     * 알림톡 발송
     */
    @PostMapping("/send")
    public ResponseEntity<SendKakaoResponse> sendKakao(@Valid @RequestBody SendKakaoRequest request) {
        log.debug("알림톡 발송 요청. type={}, targetCount={}, templateCode={}",
                request.getType(), request.getTargets().size(), request.getTemplateCode());

        List<KakaoTarget> targets = request.getTargets().stream()
                .map(t -> new KakaoTarget(t.getUserId(), t.getPhoneNumber()))
                .toList();

        List<Long> notificationIds = sendKakaoUseCase.sendKakao(
                request.getType(),
                targets,
                request.getTemplateCode(),
                request.getVariables()
        );

        return ResponseEntity.ok(SendKakaoResponse.success(notificationIds));
    }
}
