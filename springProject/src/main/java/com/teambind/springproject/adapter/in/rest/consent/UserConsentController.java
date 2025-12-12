package com.teambind.springproject.adapter.in.rest.consent;

import com.teambind.springproject.adapter.in.rest.consent.dto.NightAdConsentRequest;
import com.teambind.springproject.adapter.in.rest.consent.dto.UserConsentResponse;
import com.teambind.springproject.application.port.in.UserConsentUseCase;
import com.teambind.springproject.common.exceptions.application.UserConsentNotFoundException;
import com.teambind.springproject.domain.model.consent.UserConsent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 동의 정보 REST Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/consents")
@RequiredArgsConstructor
public class UserConsentController {

    private final UserConsentUseCase userConsentUseCase;

    /**
     * 사용자 동의 정보 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserConsentResponse> getUserConsent(@PathVariable String userId) {
        log.debug("Get user consent: userId={}", userId);

        UserConsent userConsent = userConsentUseCase.getUserConsent(userId)
                .orElseThrow(() -> new UserConsentNotFoundException(userId));

        return ResponseEntity.ok(UserConsentResponse.fromDomain(userConsent));
    }

    /**
     * 야간 광고 동의 변경
     */
    @PutMapping("/{userId}/night-ad")
    public ResponseEntity<UserConsentResponse> changeNightAdConsent(
            @PathVariable String userId,
            @Valid @RequestBody NightAdConsentRequest request) {
        log.debug("Change night ad consent: userId={}, consented={}", userId, request.getConsented());

        try {
            userConsentUseCase.changeNightAdConsent(userId, request.getConsented());
        } catch (IllegalArgumentException e) {
            throw new UserConsentNotFoundException(userId);
        }

        UserConsent userConsent = userConsentUseCase.getUserConsent(userId)
                .orElseThrow(() -> new UserConsentNotFoundException(userId));

        return ResponseEntity.ok(UserConsentResponse.fromDomain(userConsent));
    }
}
