package com.teambind.springproject.application.port.out;

public interface EmailPort {

    void send(String to, String subject, String htmlContent);

    void sendVerificationEmail(String to, String code);
}
