package com.example.student_management.service;

import com.example.student_management.entity.Student;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.context.Context;
import javax.swing.*;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine templateEngine){
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    //this will be used to send an email to the student just registered about their registration
    public void sendRegistrationEmail(String email, String name){

        try {
            //set variables for the email template
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("email", email);

            //convert the template to html
            String emailTemplate = templateEngine.process("email/register", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("vincentmandola77@gmail.com");
            helper.setTo(email);
            helper.setSubject("Student Application Received");
            helper.setText(emailTemplate, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send student confirmation email " + e);
        }

    }

    public void sendAdminRegisterEmail(Student student, String adminEmail){

        try {
            Context context = new Context();
            context.setVariable("studentInfo", student);

            String emailTemplate = templateEngine.process("email/register-admin", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("vincentmandola77@gmail.com");
            helper.setTo(adminEmail);
            helper.setSubject("New Student Registered");
            helper.setText(emailTemplate, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Message failed to send: " + e);
        }

    }
}
