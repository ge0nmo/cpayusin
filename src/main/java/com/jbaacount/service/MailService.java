package com.jbaacount.service;

import com.jbaacount.global.exception.BusinessLogicException;
import com.jbaacount.global.exception.ExceptionMessage;
import com.jbaacount.payload.request.member.SendVerificationCodeRequest;
import com.jbaacount.repository.RedisRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static com.jbaacount.service.UtilService.generateVerificationCode;

@RequiredArgsConstructor
@Slf4j
@Service
public class MailService
{
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    private final RedisRepository redisRepository;

    public String sendVerificationCode(SendVerificationCodeRequest request)
    {
        String email = request.getEmail();

        String verificationCode = generateVerificationCode();
        redisRepository.saveEmailAndVerificationCodeWith5Minutes(email, verificationCode);
        return sendVerificationEmail(email, verificationCode);
    }

    private String sendVerificationEmail(String email, String verificationCode)
    {
        try {
            MimeMessage mimeMessage = createMimeMessage(email, verificationCode);
            mailSender.send(mimeMessage);
            return "인증코드가 발송되었습니다. 5분 내로 인증을 완료해주세요.";
        } catch (MessagingException e) {
            log.error("Error sending verification email to {}", email, e);
            throw new BusinessLogicException(ExceptionMessage.MAIL_ERROR);
        }
    }

    private MimeMessage createMimeMessage(String email, String verificationCode) throws MessagingException
    {
        Context context = new Context();
        context.setVariable("verificationCode", verificationCode);
        String htmlContent = templateEngine.process("mailTemplate", context);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
        helper.setTo(email);
        helper.setSubject("Email Verification Code");
        helper.setText(htmlContent, true);

        return mimeMessage;
    }
}
