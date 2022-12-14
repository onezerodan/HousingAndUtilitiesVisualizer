package HousingAndUtilitiesVisualizer.bot;


import HousingAndUtilitiesVisualizer.model.*;
import HousingAndUtilitiesVisualizer.repository.UserRepository;
import HousingAndUtilitiesVisualizer.service.*;
import javassist.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
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
    AddressService addressUtil;

    @Autowired
    TimeService timeService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MetricsService metricsService;

    @Autowired
    ChartService chartService;

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

            log.info("NEW MESSAGE from: " + chatId + " text: " + text);






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
                            saveMetrics(update.getMessage());
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }
                    }


                }
                return;
            }
        }

        else if (update.hasCallbackQuery()) {
            try {
                handleCallbackQuery(update.getCallbackQuery());
            } catch (TelegramApiException | IOException | NotFoundException e) {
                log.error(e.getMessage(), e);
            }
        }


    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException, IOException, NotFoundException {
        Long chatId = callbackQuery.getFrom().getId();
        String callbackData = callbackQuery.getData();

        User user = userRepository.findByChatId(chatId).orElse(null);
        if (user == null) userRepository.save(new User(chatId));


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
                            ?????????????????????? ???????????????? ???? ??????????????.
                            ???????????????????? ?????????????? ?????????? ???????? ?????? ???????????????? ??????????????.
                            ????????????????, ?????????? ?????????? ?????????? ???? ?????????????????? :(
                            """;
                }

                user.setAddress(callbackData.split(":")[1]);
                userRepository.save(user);

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

            case "stat" -> {
                String data = callbackData.split(":")[1];
                Period period = null;
                switch (data) {
                    case "month" -> period = Period.MONTH;
                    case "three_months" -> period = Period.THREE_MONTHS;
                    case "six_months" -> period = Period.SIX_MONTHS;
                    case "year" -> period = Period.YEAR;
                    case "all_time" -> period = Period.ALL_TIME;
                }
                sendStatImage(chatId, period);
            }

            case "moving" -> {
                String data = callbackData.split(":")[1];
                switch (data) {
                    case "address" -> {
                        user.setAddress(null);
                        userRepository.save(user);
                        sendMsg(chatId, "?????????? ????????????. " +
                                "?????????? ???????????????????? ??????????, ???????????? ?????????????????? ?????????????? /uk");
                    }

                    case "metrics" -> {
                        metricsService.deleteAllByUserId(chatId);
                        sendMsg(chatId, "?????? ?????????????????? ??????????????.");
                    }

                    case "both" -> {
                        user.setAddress(null);
                        userRepository.save(user);
                        metricsService.deleteAllByUserId(chatId);
                        sendMsg(chatId, "?????????? ?? ?????????????????? ??????????????. " +
                                "?????????????????? ???????????????????????? ?????????? ?? ?????????????? ?????????? :)");
                    }
                }

            }
        }
        sendAnswerCallbackQuery("", false, callbackQuery);
    }

    private void saveMetrics(Message message) {
        Long chatId = message.getChatId();
        String text = message.getText();

        String answerText = "???????????? ??????????????????.";

        Class metricsClass = null;

        switch (userState.get(chatId)) {
            case ENTER_COLD_WATER_METRICS -> {
                metricsClass = ColdWaterMetrics.class;
                //metricsService.save(parseMetrics(ColdWaterMetrics.class, text, chatId));
            }

            case ENTER_HOT_WATER_METRICS -> {
                metricsClass = HotWaterMetrics.class;
                //metricsService.save(parseMetrics(HotWaterMetrics.class, text, chatId));
            }

            case ENTER_HEATING_METRICS -> {
                metricsClass = HeatingMetrics.class;
                //metricsService.save(parseMetrics(HeatingMetrics.class, text, chatId));
            }

            case ENTER_ELECTRIC_POWER_METRICS -> {
                metricsClass = ElectricPowerMetrics.class;
                //metricsService.save(parseMetrics(ElectricPowerMetrics.class, text, chatId));
            }

            default -> {
                answerText = "?????????????????? ????????????.";
                return;
            }
        }
        Metrics parsedMetrics = null;
        try {
            parsedMetrics = parseMetrics(metricsClass, text, chatId);
        } catch (IllegalArgumentException e) {
            sendMsg(chatId, "???????????? ?????????????? ??????????????.");
            return;
        } catch (NotFoundException e) {
            log.error(e.getMessage(), e);
        }
        metricsService.save(parsedMetrics);
        sendMsg(chatId, answerText);
    }

    private Metrics parseMetrics(Class metricsClass, String text, Long chatId) throws IllegalArgumentException, NotFoundException {
        User user = userRepository.findByChatId(chatId).orElseThrow(() ->new NotFoundException("User not found"));
        String[] textMetrics = text.split(",");


        double metrics1 = 0;
        double metrics2 = 0;

        Date date = timeService.getCurrentDate();

        if (metricsClass == ElectricPowerMetrics.class) {
            if (textMetrics.length == 2) {
                try {
                    metrics1 = Double.parseDouble(textMetrics[0]);
                    metrics2 = Double.parseDouble(textMetrics[1]);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IllegalArgumentException();
                }
            } else if (textMetrics.length == 3) {
                try {
                    metrics1 = Double.parseDouble(textMetrics[0]);
                    metrics2 = Double.parseDouble(textMetrics[1]);
                    date = timeService.parseDateFromStr(textMetrics[2]);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IllegalArgumentException();
                }
            } else throw new IllegalArgumentException();

            return new ElectricPowerMetrics(date, metrics1, metrics2, user);

        } else {
            if (textMetrics.length == 1) {
                try {
                    metrics1 = Double.parseDouble(textMetrics[0]);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IllegalArgumentException();
                }
            } else if (textMetrics.length == 2) {
                try {
                    metrics1 = Double.parseDouble(textMetrics[0]);
                    date = timeService.parseDateFromStr(textMetrics[1]);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    throw new IllegalArgumentException();
                }
            } else throw new IllegalArgumentException();

            if (metricsClass == ColdWaterMetrics.class) {
                return new ColdWaterMetrics(date, metrics1, user);
            } else if (metricsClass == HotWaterMetrics.class) {
                return new HotWaterMetrics(date, metrics1, user);
            } else if (metricsClass == HeatingMetrics.class) {
                return new HeatingMetrics(date, metrics1, user);
            }
        }
        return null;

    }



    private void handleIncomingCommand(Message message) throws TelegramApiException {
        Long chatId = message.getChatId();
        String text = message.getText().toLowerCase();

        switch (text) {
            case "/start" -> {

                User user = userRepository.findByChatId(chatId).orElse(null);
                if (user == null) userRepository.save(new User(chatId));
            }
            case "/enter" -> {
                execute(onMetricsChooseCommand(chatId));
                userState.remove(chatId);
            }
            case "/stat" -> {
                execute(onPeriodChooseCommand(chatId));
            }
            case "/moving" -> {
                execute(onMovingObtionsChooseCommand(chatId));
            }
            case "/uk" -> {
                User user = userRepository.findByChatId(chatId).orElse(null);

                if (user != null) {
                    String mngmtCompanyUrl = user.getAddress();
                    if (mngmtCompanyUrl != null) {
                        sendMsg(chatId, addressUtil.getManagementCompanyInfo(mngmtCompanyUrl));
                        return;
                    }
                }
                userState.put(chatId, UserState.ENTER_ADDRESS);
                sendMsg(chatId, "?????????????? ?????????? ????????.");
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

    private SendMessage onMovingObtionsChooseCommand(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyMarkup(getMovingOptionsKeyboard());
        sendMessage.setText("???????????????? ????????????, ?????????????? ???? ???????????? ??????????????.");

        return sendMessage;
    }

    private SendMessage onMetricsInputCommand(Long chatId, String chosenMetrics) {

        String text = """
                    ?????????????? ?????????????????? ?? ??????????????:
                    *xxx.xxx, dd-mm-yyyy*,
                    ??????
                    *xxx.xxx - ?????????????????? ??????????????,*
                    *dd-mm-yyyy - ???????? ???????????? ??????????????????*.
                    
                    ???????? ???? ?????????????? ???????????? ??????????????????, ???????? ?????????? ?????????????? ?????????????????????? ??????????????????????????.
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
                    ?????????????? ?????????????????? ?? ??????????????:
                    *xxx.xxx, yyy.yyy, dd-mm-yyyy*,
                    ??????
                    *xxx.xxx - ?????????????????? ?????????????? ???? ????????*,
                    *yyy.yyy - ?????????????????? ?????????????? ???? ????????*,
                    *dd-mm-yyyy - ???????? ???????????? ??????????????????*.
                                        
                    ???????? ???? ?????????????? ???????????? ??????????????????, ???????? ?????????? ?????????????? ?????????????????????? ??????????????????????????.
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
        sendMessage.setText("?????????? ?????????????????? ???? ???????????? ?????????????");

        return sendMessage;
    }

    private SendMessage onPeriodChooseCommand(Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        sendMessage.setReplyMarkup(getPeriodChooseKeyboard());
        sendMessage.setText("???????????????? ????????????, ???? ?????????????? ???? ???????????? ???????????????????? ????????????????????.");

        return sendMessage;
    }

    private void sendStatImage(Long chatId, Period period) throws IOException, TelegramApiException, NotFoundException {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        File chart = chartService.getChart(chatId, period);
        sendPhoto.setPhoto(new InputFile(chart));
        execute(sendPhoto);
        chartService.deleteFile(chart);
    }

    private SendMessage onAddressSuggestCommand(Long chatId, String address) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId.toString());
        InlineKeyboardMarkup inlineKeyboardMarkup = getSuggestedAddressesKeyboard(address);
        if (inlineKeyboardMarkup == null) {
            sendMessage.setText("?????? ???? ????????????.");
            return sendMessage;
        }
        sendMessage.setReplyMarkup(getSuggestedAddressesKeyboard(address));
        sendMessage.setText("???????????????? ???????????? ??????.");

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
        coldWaterButton.setText("???????????????? ????????");
        coldWaterButton.setCallbackData("metrics:coldWater");

        InlineKeyboardButton hotWaterButton = new InlineKeyboardButton();
        hotWaterButton.setText("?????????????? ????????");
        hotWaterButton.setCallbackData("metrics:hotWater");

        InlineKeyboardButton heatingButton = new InlineKeyboardButton();
        heatingButton.setText("??????????????????");
        heatingButton.setCallbackData("metrics:heating");

        InlineKeyboardButton electricPowerButton = new InlineKeyboardButton();
        electricPowerButton.setText("????????????????????????????");
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

    private InlineKeyboardMarkup getPeriodChooseKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton monthButton = new InlineKeyboardButton();
        monthButton.setText("?????????????? + ???????????????????? ????????????");
        monthButton.setCallbackData("stat:month");

        InlineKeyboardButton threeMonthsButton = new InlineKeyboardButton();
        threeMonthsButton.setText("3 ????????????");
        threeMonthsButton.setCallbackData("stat:three_months");

        InlineKeyboardButton sixMonthsButton = new InlineKeyboardButton();
        sixMonthsButton.setText("??????????????");
        sixMonthsButton.setCallbackData("stat:six_months");

        InlineKeyboardButton yearButton = new InlineKeyboardButton();
        yearButton.setText("??????");
        yearButton.setCallbackData("stat:year");

        InlineKeyboardButton allTimeButton = new InlineKeyboardButton();
        allTimeButton.setText("?????? ??????????");
        allTimeButton.setCallbackData("stat:all_time");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(monthButton);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(threeMonthsButton);

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(sixMonthsButton);

        List<InlineKeyboardButton> rowInline4 = new ArrayList<>();
        rowInline4.add(yearButton);

        List<InlineKeyboardButton> rowInline5 = new ArrayList<>();
        rowInline5.add(allTimeButton);

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);
        rowsInline.add(rowInline4);
        rowsInline.add(rowInline5);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    private InlineKeyboardMarkup getMovingOptionsKeyboard() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton removeAddressButton = new InlineKeyboardButton();
        removeAddressButton.setText("?????????????? ?????????????? ??????????");
        removeAddressButton.setCallbackData("moving:address");

        InlineKeyboardButton removeMetricsButton = new InlineKeyboardButton();
        removeMetricsButton.setText("?????????????? ?????? ??????????????????");
        removeMetricsButton.setCallbackData("moving:metrics");

        InlineKeyboardButton removeAddressAndMetricsButton = new InlineKeyboardButton();
        removeAddressAndMetricsButton.setText("?????????????? ?????????? ?? ??????????????????");
        removeAddressAndMetricsButton.setCallbackData("moving:both");

        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(removeAddressButton);

        List<InlineKeyboardButton> rowInline2 = new ArrayList<>();
        rowInline2.add(removeMetricsButton);

        List<InlineKeyboardButton> rowInline3 = new ArrayList<>();
        rowInline3.add(removeAddressAndMetricsButton);

        rowsInline.add(rowInline);
        rowsInline.add(rowInline2);
        rowsInline.add(rowInline3);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
