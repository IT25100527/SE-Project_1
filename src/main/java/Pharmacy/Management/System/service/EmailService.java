package Pharmacy.Management.System.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Value("${app.frontend.base-url}")
    private String baseUrl;


    // =========================================================
    // CUSTOMER EMAIL VERIFICATION - OTP
    // =========================================================

    public void sendVerificationCode(
            String toEmail,
            String fullName,
            String code
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(toEmail);

        message.setSubject(
                "Pharmacy Management System - Email Verification Code"
        );

        message.setText(
                "Hi " + fullName + ",\n\n" +

                        "Thank you for registering with " +
                        "Pharmacy Management System.\n\n" +

                        "Your email verification code is:\n\n" +

                        "        " + code + "\n\n" +

                        "This verification code will expire in 10 minutes.\n\n" +

                        "Please enter this code in the Pharmacy Management System " +
                        "to verify your email address and activate your account.\n\n" +

                        "If you did not create this account, please ignore this email.\n\n" +

                        "Thank you,\n" +
                        "Pharmacy Management System"
        );

        mailSender.send(message);
    }


    // =========================================================
    // STAFF WELCOME EMAIL
    // =========================================================

    public void sendStaffWelcomeEmail(
            String toEmail,
            String fullName,
            String tempPassword,
            String role
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromAddress);
        message.setTo(toEmail);

        message.setSubject(
                "Your Pharmacy Management System staff account"
        );

        message.setText(
                "Hi " + fullName + ",\n\n" +

                        "An account has been created for you as " +
                        role + ".\n\n" +

                        "Login at: " +
                        baseUrl +
                        "/manager/login.html\n\n" +

                        "Email: " +
                        toEmail + "\n" +

                        "Temporary password: " +
                        tempPassword + "\n\n" +

                        "Please log in and change your password.\n\n" +

                        "- Pharmacy Management System"
        );

        mailSender.send(message);
    }
}