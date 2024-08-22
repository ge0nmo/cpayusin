package com.cpayusin.member.repository;

import com.cpayusin.common.exception.BusinessLogicException;
import com.cpayusin.common.exception.ExceptionMessage;
import com.cpayusin.member.service.port.MailSendHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@RequiredArgsConstructor
@Slf4j
@Component
public class MailSendHelperImpl implements MailSendHelper
{
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendVerificationEmail(String email, String verificationCode)
    {
        try {
            MimeMessage mimeMessage = createMimeMessage(email, verificationCode);
            mailSender.send(mimeMessage);
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
        helper.setSubject("이메일 인증 코드");
        helper.setText(htmlContent, true);

        return mimeMessage;
    }
}
