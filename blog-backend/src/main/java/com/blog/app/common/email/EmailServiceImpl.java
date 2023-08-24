package com.blog.app.common.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendEmail(String to, String subject, String body) {
        log.info("Sending email to: " + to + " with subject: " + subject + " and body: " + body);
    }

}
