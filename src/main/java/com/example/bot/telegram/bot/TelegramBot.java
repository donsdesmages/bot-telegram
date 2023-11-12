package com.example.bot.telegram.bot;

import com.example.bot.telegram.StateEnum;
import com.example.bot.telegram.config.BotConfig;
import com.example.bot.telegram.entity.UserEntity;
import com.example.bot.telegram.model.UserModel;
import com.example.bot.telegram.repository.UserRepository;
import com.example.bot.telegram.service.ServiceEmailSender;
import com.example.bot.telegram.service.ServiceICheckingMail;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
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

@Component
@AllArgsConstructor
@JsonDeserialize
public class TelegramBot extends TelegramLongPollingBot {

    private final BotConfig botConfig;
    private final UserRepository userRepository;

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

            if (messageText.contains("/create")) {
                String sendTo = messageText.substring(messageText.indexOf(" "));
                if (!sendTo.isEmpty() && sendTo != null) {
                    saveText(sendTo);
                }
                sendMessage(chatId, "Отлично! " +
                    "Текст был создан, " +
                    "теперь нажмите кнопку Отправить");
            }
            if (messageText.contains("/mail")) {
                String mailFromUser = messageText.substring(messageText.indexOf(" "));
                letter = ServiceICheckingMail.validateTextFromUser(mailFromUser);
                if (letter.equals(ServiceICheckingMail.MAIL_VERIFICATION_IS_SUCCESSFULLY)) {
                    saveEmail(mailFromUser);
                }
                sendMessage(chatId, letter);
            }
            switch (messageText) {
                case START:
                    startCommandReceived(chatId, update.getMessage()
                        .getChat()
                        .getFirstName());
                    break;
                case SENDING_AT_MAIL:
                    ServiceEmailSender.sendToEmail();
                    messageSuccessfullySendToMail(chatId, update.getMessage()
                        .getText());
                    break;
                case HEAD_MENU:
                    greetingUsers(chatId, update.getMessage()
                        .getChat()
                        .getFirstName());
                    break;
                default:
                    sendMessage(chatId, "sdfsdsf");
                    break;
            }
        }
    }

    protected String saveEmail(String email) {
        UserModel model = UserModel.builder()
            .stateEnum(StateEnum.BASIC_STATE)
            .isActive(false)
            .email(email)
            .build();

        UserEntity userData = new UserEntity();

        userData.setEmail(model.getEmail());
        userData.setStateEnum(model.getStateEnum());

        userRepository.save(userData);
        return "User data successfully";
    }

    protected String saveText(String textFromUser) {
        UserModel userModel = UserModel.builder().text(textFromUser).build();

        UserEntity userEntity = new UserEntity();
        userEntity.setText(userModel.getText());

        userRepository.save(userEntity);
        return "Data was successfully saved";
    }

    @Bean
    public List<KeyboardButton> keyboardButtons() {
        List<KeyboardButton> buttons = new ArrayList<>();
        buttons.add(new KeyboardButton(SENDING_AT_MAIL));
        buttons.add(new KeyboardButton("Главное меню"));
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
        String answer = "Здравствуйте, " + name + ", рад знакомству с вами!" + "\n" +
            "Я помогаю людям отправлять письма на почту не выходя из телеграмма, далее будет краткая инструкция по использованию." + "\n" +
            "\n" +
            " 1. C помощью команды /mail, укажите адрес человека которому планируете отправить сообщение," +
            "\n" + " например: '/mail ivanov_ivan@gmail.com' " + "\n" + "  " + "\n" +
            " 2. Если почта указана верно, вас попросят составить письмо для отправки ";
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
