package com.blog.app.common.email;

public interface EmailService {
    void sendEmail(String to, String subject, String body) ;
    void sendEmailWithHtml(String to, String subject, String html) ;
}
