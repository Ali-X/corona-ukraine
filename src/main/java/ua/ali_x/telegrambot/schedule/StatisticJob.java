package ua.ali_x.telegrambot.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.model.Schedule;
import ua.ali_x.telegrambot.service.ScheduleService;
import ua.ali_x.telegrambot.service.StatisticJsonService;
import ua.ali_x.telegrambot.service.TelegramService;

import java.util.List;

@Component
public class StatisticJob implements Job {

    public static StatisticJob instance;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private StatisticJsonService statisticJsonService;
    @Autowired
    private TelegramService telegramService;

    @Value("${token}")
    private String token;

    public StatisticJob() {
        if (instance == null) {
            instance = this;
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Schedule> allEnabledUser = instance.scheduleService.getAllEnabledUser();

        if (!allEnabledUser.isEmpty()) {
            String statistics = instance.statisticJsonService.getStatistics();

            allEnabledUser.forEach(schedule -> {
                instance.telegramService.sendStatistic(schedule.getChatId(), statistics, instance.token);
            });

            System.out.println("Job " + this.getClass().getSimpleName() + " is executed");
        }
    }


}