package com.example.bot.telegram.service;

import com.example.bot.telegram.service.impl.ServiceEmailSender;
import com.example.bot.telegram.util.ConstDataMail;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Slf4j
@Service
@AllArgsConstructor
public class EmailSenderServiceImpl implements ServiceEmailSender {

    private final FindFromDataBaseServiceImpl serviceFindFromDatabase;

    public void sendToEmail() throws IOException {

        String email = " ";
        String userText = " ";

        List<String> emailFromDataBase = serviceFindFromDatabase.findEmail();
        List<String> textFromDataBase = serviceFindFromDatabase.findText();

        Iterator<String> mailIterator = emailFromDataBase.iterator();
        Iterator<String> textIterator = textFromDataBase.iterator();

        while (mailIterator.hasNext() && textIterator.hasNext()) {
            String mailToSend = mailIterator.next();
            String textToSend = textIterator.next();

            email = mailToSend;
            userText = textToSend;

            log.info("Email:"
                + email + " "
                + "successfully was received...",
                "Text:" + userText + " "
                    + "successfully was received...");

        }

        Properties properties = new Properties();

        properties.put("mail.smtp.host", ConstDataMail.HOST);
        properties.put("mail.smtp.port", ConstDataMail.SMTP_PORT);
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(ConstDataMail.FROM, "igrujhicweokbyze");
            }
        });
        try {
            session.setDebug(true);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(ConstDataMail.FROM));
            message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress(email));
            message.setSubject("Тема письма:");
            message.setText(userText);

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
