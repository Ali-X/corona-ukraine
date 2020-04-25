package ua.ali_x.telegrambot.bot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.ali_x.telegrambot.dao.FeedbackDao;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.dao.ScheduleDao;
import ua.ali_x.telegrambot.model.Feedback;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.service.QuarantineService;
import ua.ali_x.telegrambot.service.statistic.StatisticService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class CoronaUkraineBot extends TelegramLongPollingBot {

    public static final String STATISTIC_QUESTION_UKRAINE = "Яка статистика в Україні?";
    public static final String STATISTIC_QUESTION_WORLD = "Яка статистика в світі?";
    public static final String QUARANTINE_QUESTION = "Коли закінчиться карантин?";
    public static final String FEEDBACK_QUESTION = "Як залишити відгук?";
    public static final String FEEDBACK_ANSWER_START_UA = "відгук";
    public static final String FEEDBACK_ANSWER_START_RU = "отзыв";
    public static final String FEEDBACK_ANSWER_START_EN = "feedback";

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    @Qualifier("statisticJsonUkraineService")
    private StatisticService statisticJsonUkraineService;

    @Autowired
    @Qualifier("statisticHtmlUkraineService")
    private StatisticService statisticHtmlUkraineService;

    @Autowired
    @Qualifier("statisticJsonUkraineRegionService")
    private StatisticService statisticJsonUkraineRegionService;

    @Autowired
    @Qualifier("statisticJsonWorldService")
    private StatisticService statisticJsonWorldService;

    @Autowired
    private QuarantineService quarantineService;

    @Autowired
    private FeedbackDao feedbackDao;

    @Value("${botname}")
    private String botname;

    @Value("${token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
//            writeLogs(message);

            SendMessage response = fetchUserResponse(message);

            if (StringUtils.isEmpty(response.getText())) {
                response.setText(messageTemplateDao.findFirstByCode("wrong_request").getMessage());
            }

            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void writeLogs(Message message) {
/*        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        writeToFile(formatter.format(date) + ": " + message.getFrom().getFirstName() + " answered " + message.getText() + "\n");*/
    }

    private void writeToFile(String text) {
        try {
            Files.write(Paths.get("C:\\Users\\alina\\Desktop\\telegrambot_logs.txt"), text.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SendMessage fetchUserResponse(Message message) {
        SendMessage response = new SendMessage();
        String userResponse = message.getText();

        response.setChatId(message.getChatId());

        registerNewUser(message);

        switch (userResponse) {
            case "/start": {
                getWelcomeResponseText(response);
                setButtons(response);
                break;
            }
            case STATISTIC_QUESTION_UKRAINE: {
                getStatisticsUkraineResponseText(response);
                setButtons(response);
                break;
            }
            case STATISTIC_QUESTION_WORLD: {
                getStatisticsWorldResponseText(response);
                setButtons(response);
                break;
            }

            case QUARANTINE_QUESTION: {
                getDaysLeftResponseText(response);
                setButtons(response);
                break;
            }
            case FEEDBACK_QUESTION: {
                getFeedbackQuestionText(response);
                setButtons(response);
                break;
            }
            default:
                setButtons(response);
                break;
        }

        if (StringUtils.startsWithIgnoreCase(userResponse, FEEDBACK_ANSWER_START_UA)
                || StringUtils.startsWithIgnoreCase(userResponse, FEEDBACK_ANSWER_START_RU)
                || StringUtils.startsWithIgnoreCase(userResponse, FEEDBACK_ANSWER_START_EN)) {
            saveFeedback(message);
            getFeedbackAnswerText(response);
        }

        return response;
    }

    private void saveFeedback(Message message) {
        String messageText = message.getText();

        if (StringUtils.isNoneBlank(messageText)) {
            Feedback feedback = new Feedback();
            feedback.setFeedback(messageText);
            feedback.setUsername(message.getFrom().getUserName());
            feedback.setChatId(message.getChatId());
            feedback.setDate(new Date());

            feedbackDao.save(feedback);
        }
    }

    private void getFeedbackQuestionText(SendMessage response) {
        response.setText(messageTemplateDao.findFirstByCode("feedback_question").getMessage());
    }

    private void getFeedbackAnswerText(SendMessage response) {
        response.setText(messageTemplateDao.findFirstByCode("feedback_answer").getMessage());
    }

    private void getDaysLeftResponseText(SendMessage response) {
        response.setText(quarantineService.getDaysLeftMessage());
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(STATISTIC_QUESTION_UKRAINE));
        keyboardFirstRow.add(new KeyboardButton(STATISTIC_QUESTION_WORLD));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(QUARANTINE_QUESTION));
        keyboardSecondRow.add(new KeyboardButton(FEEDBACK_QUESTION));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void getWelcomeResponseText(SendMessage response) {
        String welcomeTxt = "Вас вітає Corona Ukraine Bot!";
        response.setText(welcomeTxt);
    }

    private void registerNewUser(Message message) {
        if (scheduleDao.findByChatId(message.getChatId()) == null) {
            Schedule scheduleObj = new Schedule();
            scheduleObj.setChatId(message.getChatId());
            scheduleObj.setName(message.getFrom().getUserName());
            scheduleObj.setCron("0 0 10 1/1 * ? *");
            scheduleObj.setEnabled(true);

            scheduleDao.save(scheduleObj);
        }
    }

    private void getStatisticsUkraineResponseText(SendMessage response) {
        StringBuilder sb = new StringBuilder();
        String statistics = statisticHtmlUkraineService.getStatistics();

        if (StringUtils.isEmpty(statistics)) {
            statistics = statisticJsonUkraineService.getStatistics();
        }

        sb.append(statistics);

        String regionStatistic = statisticJsonUkraineRegionService.getStatistics();

        if (StringUtils.isNoneBlank(regionStatistic)) {
            sb.append("\n\n");
            sb.append(regionStatistic);
        }

        response.setParseMode("HTML");
        response.setText(sb.toString());
    }

    private void getStatisticsWorldResponseText(SendMessage response) {
        String statistics = statisticJsonWorldService.getStatistics();

        response.setParseMode("HTML");
        response.setText(statistics);
    }

    @Override
    public String getBotUsername() {
        return botname;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
