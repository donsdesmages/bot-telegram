package com.example.bot.telegram.service;

import com.example.bot.telegram.exception.BadEmailException;
import com.example.bot.telegram.util.ConstRegx;
import com.example.bot.telegram.util.ConstantLog;

import com.example.bot.telegram.util.ConstantsUI;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.bot.telegram.util.ConstantsUI.MAIL_VERIFICATION_IS_SUCCESSFULLY;


@Slf4j
@Service
@Builder
@RequiredArgsConstructor
public class CheckingMailServiceImpl {

    public static String validateTextFromUser(String email) {

        try {
           emailValidator(email);
           log.info(ConstantLog.DATA_VALID);
        } catch (BadEmailException e) {
            log.error(ConstantLog.EXCEPTION_TEXT_EMAIL);
            return ConstantsUI.NOT_EMAIL + email;
        }
        return MAIL_VERIFICATION_IS_SUCCESSFULLY ;
    }

    public static boolean emailValidator(String email) throws BadEmailException {
        String emailStrip = email.strip();
        if (!emailStrip.isEmpty() && emailStrip != null) {
            if (emailStrip.matches(ConstRegx.EMAIL_REGULAR_EXPRESSION)) {
                log.info("The message is email...");
                return true;
            }
            log.error(ConstantLog.EMAIL_IS_NOT_VALID
                + emailStrip
                + LocalDateTime.now());

            throw new BadEmailException();

        }
        log.error(ConstantLog.EMAIL_IS_NOT_VALID
            + emailStrip
            + LocalDateTime.now());

        throw new BadEmailException();
    }
}
