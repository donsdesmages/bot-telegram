package com.example.bot.telegram.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import static com.example.bot.telegram.util.ConstantsUI.NO_DATA_SPECIFIED;
import static com.example.bot.telegram.util.ConstantsUI.OK_STATUS;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckingTheServiceServiceImpl {
    private final FindFromDataBaseServiceImpl fromDataBaseService;

    public String checkingDataAcquisitionService() {
        if (fromDataBaseService.findEmail().isEmpty() && fromDataBaseService.findText().isEmpty()) {
            log.error("the data for sending the email was not specified by the user",
                LocalDateTime.now().toString());
            return NO_DATA_SPECIFIED;
        }

        return OK_STATUS;
    }
}
