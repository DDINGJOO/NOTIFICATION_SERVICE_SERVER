package com.teambind.springproject.adapter.out.persistence.consent;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 사용자 동의 정보 JPA Repository
 */
public interface UserConsentJpaRepository extends JpaRepository<UserConsentEntity, Long> {

    Optional<UserConsentEntity> findByUserId(String userId);

    boolean existsByUserId(String userId);
}
