package com.example.bot.telegram.bot;

import com.example.bot.telegram.config.BotConfig;
import com.example.bot.telegram.entity.TextEntity;
import com.example.bot.telegram.entity.UserEntity;
import com.example.bot.telegram.model.UserModel;
import com.example.bot.telegram.repository.TextRepository;
import com.example.bot.telegram.repository.UserRepository;
import com.example.bot.telegram.service.ServiceEmailSenderImpl;
import com.example.bot.telegram.service.ServiceFindFromDatabaseImpl;
import com.example.bot.telegram.service.ServiceICheckingMailImpl;

import com.example.bot.telegram.util.ConstantsUI;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

import static com.example.bot.telegram.StateEnum.WAIT_IN_PROCESS;

@Slf4j
@Component
@JsonDeserialize
@AllArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final TextRepository textRepository;
    private final ServiceFindFromDatabaseImpl serviceFindFromDatabase;
    private final ServiceEmailSenderImpl serviceEmailSender;

    private static final String START = "/start";
    private static final String SENDING_AT_MAIL = "Отправить";
    private static final String HEAD_MENU = "Главное меню";

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        String letter = "";

        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/mail")) {
                String mailFromUser = messageText.substring(messageText.indexOf(" "));
                letter = ServiceICheckingMailImpl.validateTextFromUser(mailFromUser);
                if (letter.equals(ConstantsUI.MAIL_VERIFICATION_IS_SUCCESSFULLY)) {
                    saveEmail(mailFromUser);
                }
                sendMessage(chatId, letter);
            }

            if (messageText.contains("/create")) {
                String sendTo = messageText.substring(messageText.indexOf(" "));
                if (!sendTo.isEmpty() && sendTo != null) {
                    saveText(sendTo);
                }
                sendMessage(chatId, ConstantsUI.TEXT_WAS_CREATED);
            }
            switch (messageText) {
                case START:
                    startCommandReceived(chatId, update.getMessage()
                        .getChat()
                        .getFirstName());
                    break;
                case SENDING_AT_MAIL:
                    serviceFindFromDatabase.findEmail();
                    serviceFindFromDatabase.findText();
                    serviceEmailSender.sendToEmail();
                    messageSuccessfullySendToMail(chatId, update.getMessage()
                        .getText());
                    break;
                case HEAD_MENU:
                    greetingUsers(chatId, update.getMessage()
                        .getChat()
                        .getFirstName());
                    break;
                default:
                    sendMessage(chatId, "");
                    break;
            }
        }
    }

    protected void saveEmail(String email) {
        UserModel model = UserModel.builder()
            .stateEnum(WAIT_IN_PROCESS)
            .email(email)
            .build();

        UserEntity userData = new UserEntity();

        userData.setEmail(model.getEmail());
        userData.setStateEnum(model.getStateEnum());

        userRepository.save(userData);
        log.info("User data successfully...");
    }

    protected void saveText(String textFromUser) {
        UserModel userModel = UserModel.builder()
            .stateEnum(WAIT_IN_PROCESS)
            .text(textFromUser)
            .build();

        TextEntity textEntity = new TextEntity();
        textEntity.setText(userModel.getText());

        textRepository.save(textEntity);
        log.info("Text was successfully saved...");
    }

    @Bean
    public List<KeyboardButton> keyboardButtons() {
        List<KeyboardButton> buttons = new ArrayList<>();
        buttons.add(new KeyboardButton(SENDING_AT_MAIL));
        buttons.add(new KeyboardButton(HEAD_MENU));
        return buttons;
    }

    @Bean
    public List<KeyboardRow> keyboardRows() {
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(new KeyboardRow(keyboardButtons()));
        return rows;
    }

    @Bean
    public ReplyKeyboardMarkup replyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        replyKeyboardMarkup.setKeyboard(keyboardRows());

        return replyKeyboardMarkup;
    }

    private void startCommandReceived(Long chatId, String name) {
        String answer = "Здравствуйте, " + name + ", рад знакомству!" + "\n" +
            "Я помогаю людям отправлять письма на почту не выходя из телеграмма." + "\n" +
            "\n"  + "Вот краткая инструкция :"
            + "\n" + "\n" +
            "  C помощью команды /mail укажите адрес человека,которому " + "\n"
            + "    планируете отправить сообщение." +
            "\n" + "    Например: '/mail ivanov_ivan@gmail.com' ";
        sendMessage(chatId, answer);
    }

    private void messageSuccessfullySendToMail(Long chatId, String name) {
        String answer = "Ваше письмо было отправлено на указанную почту" ;
        sendMessage(chatId, answer);
    }

    private void sendMessage(Long chatId, String textToSend) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        sendMessage.setReplyMarkup(replyKeyboardMarkup());
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Not sending");
        }
    }

    public void greetingUsers(Long chatId, String name){
        String answer =  name + "." + "Вы в главном меню, выберете действия" + "\n" + "" + "" +
            "1. Отправить сообщение" + "\n" + "" + "" +
            "2. Оставить жалобу на сервис";
        sendMessage(chatId, answer);
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
