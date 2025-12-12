package com.teambind.springproject.adapter.out.persistence.consent;

import com.teambind.springproject.application.port.out.UserConsentPort;
import com.teambind.springproject.domain.model.consent.UserConsent;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 사용자 동의 정보 Persistence Adapter
 */
@Repository
@RequiredArgsConstructor
public class UserConsentPersistenceAdapter implements UserConsentPort {

    private final UserConsentJpaRepository userConsentJpaRepository;

    @Override
    public UserConsent save(UserConsent userConsent) {
        UserConsentEntity entity = userConsentJpaRepository.findByUserId(userConsent.getUserId())
                .map(existing -> {
                    existing.updateConsent(
                            userConsent.getConsentSettings().isServiceConsent(),
                            userConsent.getConsentSettings().isMarketingConsent(),
                            userConsent.getConsentSettings().isNightAdConsent()
                    );
                    return existing;
                })
                .orElseGet(() -> UserConsentEntity.fromDomain(userConsent));

        UserConsentEntity saved = userConsentJpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<UserConsent> findByUserId(String userId) {
        return userConsentJpaRepository.findByUserId(userId)
                .map(UserConsentEntity::toDomain);
    }

    @Override
    public boolean existsByUserId(String userId) {
        return userConsentJpaRepository.existsByUserId(userId);
    }
}
