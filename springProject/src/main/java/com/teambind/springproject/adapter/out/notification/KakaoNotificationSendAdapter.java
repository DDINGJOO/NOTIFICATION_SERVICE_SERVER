package com.teambind.springproject.adapter.out.notification;

import com.teambind.springproject.application.port.out.KakaoPort;
import com.teambind.springproject.application.port.out.NotificationSendPort;
import com.teambind.springproject.domain.model.notification.NotificationChannel;
import com.teambind.springproject.domain.model.notification.NotificationHistory;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 카카오 알림톡 발송 어댑터 (재시도용)
 */
@Component
@RequiredArgsConstructor
public class KakaoNotificationSendAdapter implements NotificationSendPort {

    private static final Pattern TEMPLATE_PATTERN = Pattern.compile("\\[템플릿:(.+?)\\]");

    private final KakaoPort kakaoPort;

    @Override
    public void send(NotificationHistory history) {
        String content = history.getContent();
        String templateCode = extractTemplateCode(content);

        // 재시도 시에는 원래 템플릿 코드만 추출, 변수는 빈 맵으로 전달
        // 실제 운영에서는 별도 저장된 변수를 사용해야 함
        Map<String, String> variables = new HashMap<>();

        kakaoPort.send(history.getRecipient(), templateCode, variables);
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return channel == NotificationChannel.KAKAO;
    }

    private String extractTemplateCode(String content) {
        Matcher matcher = TEMPLATE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "DEFAULT";
    }
}
