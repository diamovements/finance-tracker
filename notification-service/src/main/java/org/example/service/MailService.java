package org.example.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String sender;
    private final VelocityEngine velocityEngine;

    public void sendMail(String to, String subject, String path, String templateName, Map<String, Object> model) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            StringWriter stringWriter = new StringWriter();
            Template template = velocityEngine.getTemplate("templates/velocity/" + templateName);
            VelocityContext context = new VelocityContext(model);
            template.merge(context, stringWriter);

            helper.setSubject(subject);
            helper.setTo(to);
            helper.setText(stringWriter.toString(), true);
            helper.addInline("image", new ClassPathResource(path));
            log.info("Receiver: {}", to);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send email", e);
            throw new IllegalStateException("Failed to send email");
        }
    }
}
