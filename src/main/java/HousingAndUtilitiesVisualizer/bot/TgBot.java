package HousingAndUtilitiesVisualizer.bot;


import HousingAndUtilitiesVisualizer.service.TimeService;
import HousingAndUtilitiesVisualizer.util.AddressUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class TgBot extends TelegramLongPollingBot {

    @Autowired
    BotConfig botConfig;

    @Autowired
    AddressUtil addressUtil;

    @Autowired
    TimeService timeService;

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
            String text = update.getMessage().getText();

            log.info("NEW MESSAGE from: " + chatId + " text:" + text);

            // Check if user exists in database by chatId
            // . . .
            // if not exists: ask for address and add link to database




            if (text.startsWith("/")) {
                try {
                    handleIncomingCommand(update.getMessage());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                return;
            }

            if (userState.containsKey(chatId)) {
                switch (userState.get(chatId)) {
                    case ENTER_ADDRESS -> {
                        try {
                            execute(onAddressSuggestCommand(chatId, text));
                        } catch (TelegramApiException e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                    case ENTER_COLD_WATER_METRICS, ENTER_ELECTRIC_POWER_METRICS, ENTER_HOT_WATER_METRICS, ENTER_HEATING_METRICS -> {
                        try {
                            saveCommonMetrics(update.getMessage());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }
                return;
            }
        }

        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }


    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getFrom().getId();
        String callbackData = callbackQuery.getData();
        switch (callbackData.split(":")[0]) {
            case "addr" -> {
                String managementCompanyInfo = null;
                try {
                    managementCompanyInfo =
                            addressUtil.getManagementCompanyInfo(callbackData.split(":")[1]);
                } catch (NullPointerException e) {
                    log.warn(e.getMessage(), e);
                    managementCompanyInfo =
                            """
                            Управляющая компания не найдена.
                            Попробуйте выбрать адрес дома без указания корпуса.
                            Возможно, вашим домом никто не управляет :(
                            """;
                }
                sendMsg(chatId, managementCompanyInfo);
            }

            case "metrics" -> {
                String data = callbackData.split(":")[1];
                try {
                    execute(onMetricsInputCommand(chatId, data));
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        }
    }

    private void saveCommonMetrics(Message message) throws ParseException {
        Long chatId = message.getChatId();
        String[] text = message.getText().split(",");
        // user = userRepository.find(chatId)

        double metrics1 = 0;
        double metrics2 = 0;

        Date date = timeService.getCurrentDate();



        // save data
        switch (userState.get(chatId)) {
            case ENTER_COLD_WATER_METRICS -> {
                if (text.length == 1) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                else if (text.length == 2) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                        date = timeService.parseDateFromStr(text[1]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                // save
                log.info("NEW COLD WATER METRICS from: " + chatId +
                        " value: " + metrics1 +
                        " date: " + date);
            }

            case ENTER_HOT_WATER_METRICS -> {
                if (text.length == 1) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                else if (text.length == 2) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                        date = timeService.parseDateFromStr(text[1]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                log.info("NEW HOT WATER METRICS from: " + chatId +
                        " value: " + metrics1 +
                        " date: " + date);
            }

            case ENTER_HEATING_METRICS -> {
                if (text.length == 1) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                else if (text.length == 2) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                        date = timeService.parseDateFromStr(text[1]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                log.info("NEW HEATING METRICS from: " + chatId +
                        " value: " + metrics1 +
                        " date: " + date);
            }

            case ENTER_ELECTRIC_POWER_METRICS -> {

                if (text.length == 2) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                        metrics2 = Double.parseDouble(text[1]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                else if (text.length == 3) {
                    try {
                        metrics1 = Double.parseDouble(text[0]);
                        metrics2 = Double.parseDouble(text[1]);
                        date = timeService.parseDateFromStr(text[2]);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                }
                log.info("NEW ELECTRIC POWER METRICS from: " + chatId +
                        " value day: " + metrics1 +
                        "value night: " + metrics2 +
                        " date: " + date);
            }
        }




    }

    private void handleIncomingMetric(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();
        switch (userState.get(chatId)) {
           // case ENTER_COLD_WATER_METRICS ->
        }
    }

    private void handleIncomingCommand(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();

        switch (text) {
            case "/enter" -> {
                execute(onMetricsChooseCommand(chatId));
                userState.remove(chatId);
            }
            case "/uk" -> {
                // if user exists
                //  get info from link stored in database
                // . . .
                // else suggest addresses from user input
                userState.put(chatId, UserState.ENTER_ADDRESS);
                sendMsg(chatId, "Введите адрес дома.");

            }

        }
    }

    private void sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) throws TelegramApiException{
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        execute(answerCallbackQuery);
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

    private SendMessage onMetricsInputCommand(Long chatId, String chosenMetrics) {

        String text = """
                    Введите показания в формате:
                    *xxx.xxx, dd-mm-yyy*,
                    где
                    *xxx.xxx - показания прибора,*
                    *dd-mm-yyyy - дата снятия показаний*.
                    
                    Если вы укажете только показания, дата будет выбрана сегодняшней автоматически.
                    """;

        switch (chosenMetrics) {

            case "coldWater" -> {
                userState.put(chatId, UserState.ENTER_COLD_WATER_METRICS);
            }

            case "hotWater" -> {
                userState.put(chatId, UserState.ENTER_HOT_WATER_METRICS);
            }

            case "heating" -> {
                userState.put(chatId, UserState.ENTER_HEATING_METRICS);
            }

            case "electricPower" -> {
                userState.put(chatId, UserState.ENTER_ELECTRIC_POWER_METRICS);
                text = """
                    Введите показания в формате:
                    *xxx.xxx, yyy.yyy, dd-mm-yyy*,
                    где
                    *xxx.xxx - показания прибора за день*,
                    *yyy.yyy - показания прибора за ночь*,
                    *dd-mm-yyyy - дата снятия показаний*.
                                        
                    Если вы укажете только показания, дата будет выбрана сегодняшней автоматически.
                    """;
            }
        }

        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);

        return sendMessage;
    }

    private SendMessage onMetricsChooseCommand(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyMarkup(getMetricsChooseKeyboard());
        sendMessage.setText("Какие показания вы хотите внести?");

        return sendMessage;
    }

    private SendMessage onAddressSuggestCommand(Long chatId, String address) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        InlineKeyboardMarkup inlineKeyboardMarkup = getSuggestedAddressesKeyboard(address);
        if (inlineKeyboardMarkup == null) {
            sendMessage.setText("Дом не найден.");
            return sendMessage;
        }
        sendMessage.setReplyMarkup(getSuggestedAddressesKeyboard(address));
        sendMessage.setText("Выберете нужный дом.");

        return sendMessage;

    }

    private InlineKeyboardMarkup getSuggestedAddressesKeyboard(String inputAddress) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        Map<String, String> suggestedAddresses = null;
        try {
            suggestedAddresses = addressUtil.findAllMatchingAddresses(inputAddress);
        } catch (NullPointerException e) {
            log.warn(e.getMessage());
            return null;
        }

        for (Map.Entry<String, String> addressEntry : suggestedAddresses.entrySet()) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(addressEntry.getKey());
            button.setCallbackData("addr:" + addressEntry.getValue());

            List<InlineKeyboardButton> rowInline = new ArrayList<>();

            rowInline.add(button);
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }


    private InlineKeyboardMarkup getMetricsChooseKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton coldWaterButton = new InlineKeyboardButton();
        coldWaterButton.setText("Холодная вода");
        coldWaterButton.setCallbackData("metrics:coldWater");

        InlineKeyboardButton hotWaterButton = new InlineKeyboardButton();
        hotWaterButton.setText("Горячая вода");
        hotWaterButton.setCallbackData("metrics:hotWater");

        InlineKeyboardButton heatingButton = new InlineKeyboardButton();
        heatingButton.setText("Отопление");
        heatingButton.setCallbackData("metrics:heating");

        InlineKeyboardButton electricPowerButton = new InlineKeyboardButton();
        electricPowerButton.setText("Электроэнергия");
        electricPowerButton.setCallbackData("metrics:electricPower");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(coldWaterButton);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(hotWaterButton);

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(heatingButton);

        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        rowInline4.add(electricPowerButton);

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
