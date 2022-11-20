package HousingAndUtilitiesVisualizer.bot;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TgBot extends TelegramLongPollingBot {

    @Autowired
    BotConfig botConfig;

    Logger log = LogManager.getLogger(TgBot.class);

    private final ConcurrentHashMap<Long, UserState> userState = new ConcurrentHashMap<>();

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            // Check if user exists in database by chatId
            // . . .
            // if not exists: ask for address and add to database
            if (userState.contains(chatId)) {
                handleIncomingMetric(update.getMessage());
            }
            else {
                try {
                    handleIncomingCommand(update.getMessage());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }



        }


    }

    private void parseCommonMetric(Message message) {

    }

    private void handleIncomingMetric(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();
        switch (userState.get(chatId)) {
            case ENTER_COLD_WATER_METRICS ->
        }
    }

    private void handleIncomingCommand(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();

        switch (text) {
            case "/enter":
                execute(onMetricsInputCommand(chatId, "Какия показания вы хотите сдать?"));
                userState.remove(chatId);
                break;

            case "холодная вода":
                sendMsg(chatId, """
                        Введите показания в формате **xxx.xxx, dd.mm.yyyy**,
                        где xxx.xxx - показание,
                        dd.mm.yyyy - дата снятия.
                        
                        Если отправите покзаание без даты - она будет автоматически будет указана сегодняшней.
                        """);
                userState.put(chatId, UserState.ENTER_COLD_WATER_METRICS);
                break;

        }
    }


    private void sendMsg(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdown(true);

        try {
            execute(sendMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private SendMessage onMetricsInputCommand(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyMarkup(getMetricsInputKeyboard());
        sendMessage.setText(text);

        return sendMessage;
    }

    private static ReplyKeyboardMarkup getMetricsInputKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        KeyboardButton coldWaterButton = new KeyboardButton();
        coldWaterButton.setText("Холодная вода");

        KeyboardButton hotWaterButton = new KeyboardButton();
        hotWaterButton.setText("Горячая вода");

        KeyboardButton heatingButton = new KeyboardButton();
        heatingButton.setText("Отопление");

        KeyboardButton electricPowerButton = new KeyboardButton();
        electricPowerButton.setText("Электроэнергия");

        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(coldWaterButton);
        keyboardFirstRow.add(hotWaterButton);
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(heatingButton);
        keyboardSecondRow.add(electricPowerButton);
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }
}
