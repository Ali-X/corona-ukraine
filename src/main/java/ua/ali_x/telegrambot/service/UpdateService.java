package ua.ali_x.telegrambot.service;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.model.Schedule;

import java.util.List;

@Service
@Component
public class UpdateService {
    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TelegramService telegramService;

    @Value("${token}")
    private String token;

    public void sendUpdateMessage(Boolean isEnabled, String message) {
        if (isEnabled) {
            List<Schedule> allEnabledUser = scheduleService.getAllEnabledUser();

            allEnabledUser.forEach(schedule -> {
                telegramService.sendMessage(schedule.getChatId(), message, token);
            });
        }
    }
}
