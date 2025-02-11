package com.email.sender;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class BulkEmailSender {
    public static void main(String[] args) {
        final String senderEmail = "your-email"; // Your Gmail ID
        final String senderPassword = "password"; // Your App Password (not normal password)

        String filePath = "src/main/resources/emails.txt"; // Path to emails.txt
        String resumePath = "src/main/resources/resume.pdf"; // Path to resume

        try {
            // Read all email addresses from emails.txt
            List<String> emailLines = Files.readAllLines(Paths.get(filePath));

            for (String line : emailLines) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String recipientEmail = parts[0].trim();
                    String companyName = parts[1].trim();

                    sendEmailWithAttachment(senderEmail, senderPassword, recipientEmail, companyName, resumePath);
                    System.out.println("Email sent to: " + recipientEmail);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmailWithAttachment(String senderEmail, String senderPassword,
                                               String recipientEmail, String companyName,
                                               String resumePath) throws Exception {
        // Email properties
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");  // Force TLS 1.2
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");


        // Create session with authentication
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        // Create email message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject("Application for Java Developer Position at " + companyName);

        // Create email body
        String emailBody = "Dear Hiring Manager,\n\n" +
                "I am writing to express my interest in the Java Developer position at " + companyName +
                ". As a recent BE graduate with practical experience in Java development through my internship at BLSoftX Technologies, " +
                "combined with valuable customer service experience from my roles at Vodafone and SBI Cap Securities, " +
                "I believe I can bring a unique blend of technical skills and client-facing capabilities to your team.\n\n" +
                "Key highlights of my background:\n" +
                "- Currently interning at BLSoftX Technologies, developing core web applications\n" +
                "- Proficient in Java 8, Spring MVC, Spring Boot, and RESTful APIs\n" +
                "- Experience with MySQL and basic knowledge of NoSQL databases\n" +
                "- Strong foundation in customer service and relationship management\n" +
                "- Recently completed Full Stack Java Development certification from Ineuron\n\n" +
                "I am particularly drawn to " + companyName + "'s commitment to excellence and innovation. " +
                "My resume is attached for your detailed review.\n\n" +
                "I am available for an interview at your convenience and can be reached at 7038288265 or akshayramteke97@gmail.com.\n\n" +
                "Thank you for considering my application.\n\n" +
                "Best regards,\nAkshay Ramteke";

        // Email body part
        MimeBodyPart textBodyPart = new MimeBodyPart();
        textBodyPart.setText(emailBody);

        // Attachment part
        MimeBodyPart attachmentBodyPart = new MimeBodyPart();
        attachmentBodyPart.attachFile(new File(resumePath));

        // Combine email parts
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(textBodyPart);
        multipart.addBodyPart(attachmentBodyPart);

        // Set email content
        message.setContent(multipart);

        // Send email
        Transport.send(message);
    }
}
