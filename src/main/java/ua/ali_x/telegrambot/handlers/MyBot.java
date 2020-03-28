package ua.ali_x.telegrambot.handlers;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.dao.ScheduleDao;
import ua.ali_x.telegrambot.service.StatisticService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class MyBot extends TelegramLongPollingBot {

    @Autowired
    private ScheduleDao scheduleDao;

    @Autowired
    private StatisticService statisticService;

    @Value("${botname}")
    private String botname;

    @Value("${token}")
    private String token;

    @Override
    public void onUpdateReceived(Update update) {
        try {
            Message message = update.getMessage();
            writeLogs(message);

            SendMessage response = fetchUserResponse(message);

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
        String userResponse = message.getText();
        SendMessage response;

        if (scheduleDao.findByChatId(message.getChatId()) == null) {
            Schedule scheduleObj = new Schedule();
            scheduleObj.setChatId(message.getChatId());
            scheduleObj.setName(message.getFrom().getUserName());
            scheduleObj.setCron("0 0/1 * 1/1 * ?");
            scheduleObj.setEnabled(true);

            scheduleDao.save(scheduleObj);
        }

        response = getStatisticsResponse(message);

       /* switch (userResponse) {
            case ANSWER_YES:
                response = getYesAnswerSendMessage(message);
                break;
            case ANSWER_NO:
                response = getNoAnswerSendMessage(message);
                break;
            default:
                response = getDefaultSendMessage(message);
                break;
        }*/

        return response;
    }

    private SendMessage getStatisticsResponse(Message message) {
        SendMessage response = new SendMessage();
        response.setChatId(message.getChatId());

        String statistics = statisticService.getStatistics();

        response.setText(statistics);

        return response;
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
