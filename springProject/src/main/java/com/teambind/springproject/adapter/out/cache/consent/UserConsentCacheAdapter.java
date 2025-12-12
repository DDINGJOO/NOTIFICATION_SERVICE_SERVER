package com.teambind.springproject.adapter.out.cache.consent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teambind.springproject.application.port.out.UserConsentCachePort;
import com.teambind.springproject.domain.model.consent.UserConsent;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 사용자 동의 정보 Redis Cache Adapter
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserConsentCacheAdapter implements UserConsentCachePort {

    private static final String KEY_PREFIX = "consent:";
    private static final Duration TTL = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void save(UserConsent userConsent) {
        String key = buildKey(userConsent.getUserId());
        try {
            UserConsentCacheDto dto = UserConsentCacheDto.fromDomain(userConsent);
            String json = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set(key, json, TTL);
            log.debug("Cached user consent: userId={}", userConsent.getUserId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize user consent for cache: userId={}", userConsent.getUserId(), e);
        }
    }

    @Override
    public Optional<UserConsent> findByUserId(String userId) {
        String key = buildKey(userId);
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("Cache miss: userId={}", userId);
                return Optional.empty();
            }
            UserConsentCacheDto dto = objectMapper.readValue(value.toString(), UserConsentCacheDto.class);
            log.debug("Cache hit: userId={}", userId);
            return Optional.of(dto.toDomain());
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize user consent from cache: userId={}", userId, e);
            return Optional.empty();
        }
    }

    @Override
    public void evict(String userId) {
        String key = buildKey(userId);
        redisTemplate.delete(key);
        log.debug("Evicted user consent cache: userId={}", userId);
    }

    private String buildKey(String userId) {
        return KEY_PREFIX + userId;
    }
}
