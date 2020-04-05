package ua.ali_x.telegrambot.bot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
import ua.ali_x.telegrambot.dao.ScheduleDao;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.service.StatisticHtmlService;
import ua.ali_x.telegrambot.service.StatisticJsonService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class CoronaUkraineBot extends TelegramLongPollingBot {

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private StatisticJsonService statisticJsonService;

    @Autowired
    private StatisticHtmlService statisticHtmlService;

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

            if (StringUtils.isNoneBlank(response.getText())) {
                execute(response);
            }
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
            case "Переглянути статистику": {
                getStatisticsResponseText(response);
                setButtons(response);
                break;
            }
            default:
                setButtons(response);
                break;
        }

        return response;
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(false);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("Переглянути статистику"));

      /*  // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton(“Помощь”);
*/
        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
//        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
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
            scheduleObj.setCron("0 0/1 * 1/1 * ?");
            scheduleObj.setEnabled(true);

            scheduleDao.save(scheduleObj);
        }
    }

    private void getStatisticsResponseText(SendMessage response) {
        String statistics = statisticHtmlService.getStatistics();

        if (StringUtils.isEmpty(statistics)) {
            statistics = statisticJsonService.getStatistics();
        }

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
