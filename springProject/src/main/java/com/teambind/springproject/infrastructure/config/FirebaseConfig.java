package com.teambind.springproject.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * Firebase Cloud Messaging 설정
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.credentials-path:}")
    private String credentialsPath;

    private boolean initialized = false;

    @PostConstruct
    public void initialize() {
        if (credentialsPath == null || credentialsPath.isBlank()) {
            log.warn("Firebase credentials path가 설정되지 않았습니다. Push 알림 기능이 비활성화됩니다.");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                Optional<InputStream> serviceAccount = getCredentialsStream();

                if (serviceAccount.isEmpty()) {
                    log.warn("Firebase credentials 파일을 찾을 수 없습니다: {}. Push 알림 기능이 비활성화됩니다.", credentialsPath);
                    return;
                }

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount.get()))
                        .build();

                FirebaseApp.initializeApp(options);
                initialized = true;
                log.info("Firebase 초기화 완료");
            } else {
                initialized = true;
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패. Push 알림 기능이 비활성화됩니다.", e);
        }
    }

    private Optional<InputStream> getCredentialsStream() {
        try {
            // 클래스패스에서 먼저 시도
            ClassPathResource resource = new ClassPathResource(credentialsPath);
            if (resource.exists()) {
                return Optional.of(resource.getInputStream());
            }

            // 파일 시스템에서 시도
            java.io.File file = new java.io.File(credentialsPath);
            if (file.exists()) {
                return Optional.of(new FileInputStream(file));
            }

            return Optional.empty();
        } catch (IOException e) {
            log.warn("Firebase credentials 파일 읽기 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Bean
    public Optional<FirebaseMessaging> firebaseMessaging() {
        if (!initialized) {
            log.info("Firebase가 초기화되지 않아 FirebaseMessaging Bean을 생성하지 않습니다.");
            return Optional.empty();
        }
        return Optional.of(FirebaseMessaging.getInstance());
    }

    public boolean isInitialized() {
        return initialized;
    }
}
