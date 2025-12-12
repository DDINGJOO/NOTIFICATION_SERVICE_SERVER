package com.teambind.springproject.adapter.in.rest.kakao.dto;

import com.teambind.springproject.domain.model.notification.NotificationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 카카오 알림톡 발송 요청 DTO
 */
@Getter
@NoArgsConstructor
public class SendKakaoRequest {

    @NotNull(message = "알림 타입은 필수입니다")
    private NotificationType type;

    @NotEmpty(message = "발송 대상은 최소 1명 이상이어야 합니다")
    @Valid
    private List<KakaoTarget> targets;

    @NotBlank(message = "템플릿 코드는 필수입니다")
    private String templateCode;

    private Map<String, String> variables;
}
