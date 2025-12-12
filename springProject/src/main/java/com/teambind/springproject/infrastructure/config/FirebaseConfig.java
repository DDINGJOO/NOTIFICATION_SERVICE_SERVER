package com.teambind.springproject.infrastructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @Value("${firebase.credentials-path}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                InputStream serviceAccount = getCredentialsStream();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패", e);
        }
    }

    private InputStream getCredentialsStream() throws IOException {
        // 클래스패스에서 먼저 시도
        ClassPathResource resource = new ClassPathResource(credentialsPath);
        if (resource.exists()) {
            return resource.getInputStream();
        }

        // 파일 시스템에서 시도
        return new FileInputStream(credentialsPath);
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
