package com.example.bot.telegram.service;

import com.example.bot.telegram.exception.BadEmailException;
import com.example.bot.telegram.util.ConstRegx;
import com.example.bot.telegram.util.ConstantLogg;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class ServiceICheckingMail {

    public static String MAIL_VERIFICATION_IS_SUCCESSFULLY = "Отлично! Теперь составьте письмо для отправки, "
        + " " + "" + "\n" + " указав команду: /create 'Здесь ваш текст сообщения'";

    public static String NOT_EMAIL = "Кажется в названии почты была допущена ошибка."
        + "\n" + "Проверьте правильность названия почты и повторите отправку еще раз "
        + "\n"  + "Почта адресата:" + " ";

    public static String validateTextFromUser(String email) {

        try {
           emailValidator(email);
           log.info(ConstantLogg.DATA_VALID);
        } catch (BadEmailException e) {
            log.error(ConstantLogg.EXCEPTION_TEXT_EMAIL);
            return NOT_EMAIL
                + email
                + LocalDateTime.now();
        }
        return MAIL_VERIFICATION_IS_SUCCESSFULLY ;
    }

    public static boolean emailValidator(String email) throws BadEmailException {
        String emailStrip = email.strip();
        if (!emailStrip.isEmpty() && emailStrip != null) {
            if (emailStrip.matches(ConstRegx.EMAIL_REGULAR_EXPRESSION)) {
                log.info("This message is email...");
                return true;
            }
            log.error(ConstantLogg.EMAIL_IS_NOT_VALID
                + emailStrip
                + LocalDateTime.now());

            throw new BadEmailException();

        }
        log.error(ConstantLogg.EMAIL_IS_NOT_VALID
            + emailStrip
            + LocalDateTime.now());

        throw new BadEmailException();
    }
}
