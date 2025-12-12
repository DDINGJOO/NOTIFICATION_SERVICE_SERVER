package com.teambind.springproject.adapter.out.email;

import com.teambind.springproject.application.port.out.EmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailAdapter implements EmailPort {
	
	private static final String FROM_ADDRESS = "no-reply@teambind.co.kr";
	
	private final JavaMailSender mailSender;
	
	@Override
	public void send(String to, String subject, String htmlContent) {
		log.debug("Sending email to: {}, subject: {}", to, subject);
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
			
			helper.setTo(to);
			helper.setFrom(FROM_ADDRESS);
			helper.setSubject(subject);
			helper.setText(htmlContent, true);
			
			mailSender.send(message);
			log.info("Email sent successfully to: {}", to);
			
		} catch (MessagingException e) {
			log.error("Failed to send email to: {}", to, e);
			throw new RuntimeException("이메일 전송 실패", e);
		}
	}
	
	@Override
	public void sendVerificationEmail(String to, String code) {
		log.debug("Sending verification email to: {}, code: {}", to, code);
		
		String subject = "[BANDER] 회원가입 이메일 인증";
		String htmlContent = "<h1>이메일 인증</h1>" +
				"<p>아래 인증 코드를 해당 어플리케이션 입력란에 입력한 후 인증 버튼을 눌러주세요.</p>" +
				"<p style='font-size: 1.2em; font-weight: bold;'>인증 코드: " + code + "</p>";
		
		send(to, subject, htmlContent);
	}
}
