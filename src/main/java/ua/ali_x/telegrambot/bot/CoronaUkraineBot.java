package ua.ali_x.telegrambot.bot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.ali_x.telegrambot.dao.FeedbackDao;
import ua.ali_x.telegrambot.dao.MessageHistoryDao;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.dao.UserChatDao;
import ua.ali_x.telegrambot.model.Feedback;
import ua.ali_x.telegrambot.model.MessageHistory;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.model.UserChat;
import ua.ali_x.telegrambot.schedule.SchedulerService;
import ua.ali_x.telegrambot.service.QuarantineService;
import ua.ali_x.telegrambot.service.course.CourseService;
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

    //    questions
    public static final String GENERAL_STATISTIC_QUESTION = "Загальна статистика";
    public static final String DETAILED_STATISTIC_QUESTION = "Детальна статистика";
    public static final String STATISTIC_QUESTION_UKRAINE = "Статистика в Україні";
    public static final String STATISTIC_QUESTION_WORLD = "Статистика в світі";
    public static final String QUARANTINE_QUESTION = "Коли закінчиться карантин?";
    public static final String FEEDBACK_QUESTION = "Залишити відгук";
    public static final String SETTINGS_QUESTION = "Налаштування";
    public static final String DISABLE_NOTIFICATION = "Відключити сповіщення";
    public static final String DISABLED_NOTIFICATION = "Сповіщення відключені";
    public static final String ENABLE_NOTIFICATION = "Включити сповіщення";
    public static final String ENABLED_NOTIFICATION = "Сповіщення включені";
    public static final String CHOOSE_MENU_QUESTION = "Оберіть пункт меню.";
    public static final String COURSE_QUESTION = "Курс";
    public static final String COURSE_QUESTION_EXCHANGE = "Курс валют";
    public static final String COURSE_QUESTION_EXCHANGE_PB = "Курс Приват Банку";
    public static final String COURSE_QUESTION_EXCHANGE_NBU = "Курс НБУ";
    public static final String COURSE_QUESTION_METAL = "Курс металу";
    public static final String COURSE_QUESTION_BITCOIN = "Курс біткойну";
    public static final String COURSE_QUESTION_OIL = "Курс нафти";
    public static final String ERROR_QUESTION = "Вибачте, сталась помилка!";

    //    answers
    public static final String FEEDBACK_ANSWER_START_UA = "відгук";
    public static final String FEEDBACK_ANSWER_START_RU = "отзыв";
    public static final String FEEDBACK_ANSWER_START_EN = "feedback";

    @Autowired
    private UserChatDao userChatDao;

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private SchedulerService scheduleService;

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

    @Autowired
    @Qualifier("exchangeCoursePB")
    private CourseService courseExchangeServicePB;

    @Autowired
    @Qualifier("exchangeArchiveCourseNBU")
    private CourseService courseExchangeServiceNBU;

    @Autowired
    @Qualifier("metalCourse")
    private CourseService courseMetalService;

    @Autowired
    @Qualifier("bitcoinCoursePB")
    private CourseService courseBitcoinService;

    @Autowired
    @Qualifier("oilCourse")
    private CourseService courseOilService;

    @Autowired
    private MessageHistoryDao messageHistoryDao;

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
                setMainButtons(response);
                break;
            }
            case GENERAL_STATISTIC_QUESTION: {
                getStatisticsUkraineResponseText(response);
                setMainButtons(response);
                break;
            }
            case DETAILED_STATISTIC_QUESTION: {
                getChooseMenuResponseText(response);
                setDetailedStatisticButtons(response);
                break;
            }
            case STATISTIC_QUESTION_UKRAINE: {
                getStatisticsUkraineRegionsResponseText(response);
                setMainButtons(response);
                break;
            }
            case STATISTIC_QUESTION_WORLD: {
                getStatisticsWorldResponseText(response);
                setMainButtons(response);
                break;
            }
            case QUARANTINE_QUESTION: {
                getDaysLeftResponseText(response);
                setMainButtons(response);
                break;
            }
            case FEEDBACK_QUESTION: {
                getFeedbackQuestionText(response);
                setMainButtons(response);
                break;
            }
            case SETTINGS_QUESTION: {
                getChooseMenuResponseText(response);
                setSettingsButtons(response, message);
                break;
            }
            case COURSE_QUESTION: {
                getChooseMenuResponseText(response);
                setCourseButtons(response);
                break;
            }
            case COURSE_QUESTION_EXCHANGE: {
                getChooseMenuResponseText(response);
                setExchangeCourseButtons(response);
                break;
            }
            case COURSE_QUESTION_EXCHANGE_PB: {
                getCourseExchangePBText(response);
                setMainButtons(response);
                break;
            }
            case COURSE_QUESTION_EXCHANGE_NBU: {
                getCourseExchangeNBUText(response);
                setMainButtons(response);
                break;
            }
            case COURSE_QUESTION_METAL: {
                getCourseMetalText(response);
                setMainButtons(response);
                break;
            }
            case COURSE_QUESTION_BITCOIN: {
                getCourseBitcoinText(response);
                setMainButtons(response);
                break;
            }
            case COURSE_QUESTION_OIL: {
                getCourseOilText(response);
                setMainButtons(response);
                break;
            }
            case DISABLE_NOTIFICATION: {
                getDisableNotificationText(response, message);
                setMainButtons(response);
                break;
            }
            case ENABLE_NOTIFICATION: {
                getEnableNotificationText(response, message);
                setMainButtons(response);
                break;
            }
            default:
                setMainButtons(response);
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

    private void getEnableNotificationText(SendMessage response, Message message) {
        UserChat chat = userChatDao.findFirstByChatId(message.getChatId());

        if (chat != null && chat.getSchedule() != null) {
            scheduleService.initStatisticJob(message.getChatId(), chat.getSchedule().getCron());

            chat.getSchedule().setEnabled(Boolean.TRUE);
            userChatDao.save(chat);

            response.setText(ENABLED_NOTIFICATION);
            return;
        }

        response.setText(ERROR_QUESTION);
    }

    private void getDisableNotificationText(SendMessage response, Message message) {
        UserChat chat = userChatDao.findFirstByChatId(message.getChatId());

        if (chat != null && chat.getSchedule() != null) {
            boolean result = scheduleService.deleteStatisticJob(message.getChatId());

            if (result) {
                chat.getSchedule().setEnabled(Boolean.FALSE);
                userChatDao.save(chat);

                response.setText(DISABLED_NOTIFICATION);
                return;
            }
        }

        response.setText(ERROR_QUESTION);
    }

    private void getCourseExchangePBText(SendMessage response) {
        String course;
        sendTypingChatAction(response);

        MessageHistory coursePBMessageHistory = messageHistoryDao.findFirstByTypeOrderByDateDesc("coursePB");

        if (coursePBMessageHistory == null || coursePBMessageHistory.getMessage() == null) {
            course = courseExchangeServicePB.getCourse();
        } else {
            course = coursePBMessageHistory.getMessage();
        }

        response.setParseMode("HTML");
        response.setText(course);
    }

    private void getCourseExchangeNBUText(SendMessage response) {
        String course;
        sendTypingChatAction(response);

        MessageHistory coursePBMessageHistory = messageHistoryDao.findFirstByTypeOrderByDateDesc("courseNBU");

        if (coursePBMessageHistory == null || coursePBMessageHistory.getMessage() == null) {
            course = courseExchangeServiceNBU.getCourse();
        } else {
            course = coursePBMessageHistory.getMessage();
        }

        response.setParseMode("HTML");
        response.setText(course);
    }

    private void getCourseMetalText(SendMessage response) {
        response.setParseMode("HTML");
        response.setText(courseMetalService.getCourse());
    }

    private void getCourseBitcoinText(SendMessage response) {
        response.setParseMode("HTML");
        response.setText(courseBitcoinService.getCourse());
    }

    private void getCourseOilText(SendMessage response) {
        response.setParseMode("HTML");
        response.setText(courseOilService.getCourse());
    }

    private void sendTypingChatAction(SendMessage response) {
        try {
            execute(new SendChatAction(response.getChatId(), "typing"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void saveFeedback(Message message) {
        String messageText = message.getText();

        if (StringUtils.isNoneBlank(messageText)) {
            UserChat userChat = userChatDao.findFirstByChatId(message.getChatId());
            Feedback feedback = new Feedback();
            feedback.setFeedback(messageText);
            feedback.setUserChat(userChat);
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

    public synchronized void setMainButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(GENERAL_STATISTIC_QUESTION));
        keyboardFirstRow.add(new KeyboardButton(DETAILED_STATISTIC_QUESTION));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(COURSE_QUESTION));
        keyboardSecondRow.add(new KeyboardButton(SETTINGS_QUESTION));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void setDetailedStatisticButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(STATISTIC_QUESTION_UKRAINE));
        keyboardFirstRow.add(new KeyboardButton(STATISTIC_QUESTION_WORLD));

        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void setSettingsButtons(SendMessage sendMessage, Message message) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();

        UserChat userChat = userChatDao.findFirstByChatId(message.getChatId());

        if (userChat != null && userChat.getSchedule() != null) {
            if (userChat.getSchedule().getEnabled()) {
                keyboardFirstRow.add(new KeyboardButton(DISABLE_NOTIFICATION));
            } else {
                keyboardFirstRow.add(new KeyboardButton(ENABLE_NOTIFICATION));
            }
        }

        keyboardFirstRow.add(new KeyboardButton(FEEDBACK_QUESTION));

        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void setCourseButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(COURSE_QUESTION_EXCHANGE));
        keyboardFirstRow.add(new KeyboardButton(COURSE_QUESTION_METAL));

        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton(COURSE_QUESTION_BITCOIN));
        keyboardSecondRow.add(new KeyboardButton(COURSE_QUESTION_OIL));

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public synchronized void setExchangeCourseButtons(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton(COURSE_QUESTION_EXCHANGE_PB));
        keyboardFirstRow.add(new KeyboardButton(COURSE_QUESTION_EXCHANGE_NBU));

        keyboard.add(keyboardFirstRow);

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void getWelcomeResponseText(SendMessage response) {
        String welcomeTxt = "Вас вітає Corona Ukraine Bot!";
        response.setText(welcomeTxt);
    }

    private void registerNewUser(Message message) {
        if (userChatDao.findFirstByChatId(message.getChatId()) == null) {
            UserChat userChat = new UserChat();
            userChat.setChatId(message.getChatId());
            userChat.setUsername(message.getFrom().getUserName());
            userChat.setPhone(message.getContact() != null ? message.getContact().getPhoneNumber() : null);

            Schedule scheduleObj = new Schedule();
            scheduleObj.setCron("0 0 10 1/1 * ? *");
            scheduleObj.setEnabled(true);

            userChat.setSchedule(scheduleObj);

            userChatDao.save(userChat);

            scheduleService.initStatisticJob(userChat.getChatId(), scheduleObj.getCron());
        }
    }

    private void getStatisticsUkraineResponseText(SendMessage response) {
        StringBuilder sb = new StringBuilder();
        String statistics;

        MessageHistory statisticMessageHistory = messageHistoryDao.findFirstByTypeOrderByDateDesc("statistic");

        if (statisticMessageHistory == null || statisticMessageHistory.getMessage() == null) {
            statistics = statisticHtmlUkraineService.getStatistics();

            if (StringUtils.isEmpty(statistics)) {
                statistics = statisticJsonUkraineService.getStatistics();
            }
        } else {
            statistics = statisticMessageHistory.getMessage();
        }

        sb.append(statistics);

        response.setParseMode("HTML");
        response.setText(sb.toString());
    }

    private void getStatisticsUkraineRegionsResponseText(SendMessage response) {
        String regionStatistic = statisticJsonUkraineRegionService.getStatistics();

        response.setParseMode("HTML");
        response.setText(regionStatistic);
    }

    private void getStatisticsWorldResponseText(SendMessage response) {
        String statistics = statisticJsonWorldService.getStatistics();

        response.setParseMode("HTML");
        response.setText(statistics);
    }

    private void getChooseMenuResponseText(SendMessage response) {
        response.setText(CHOOSE_MENU_QUESTION);
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
