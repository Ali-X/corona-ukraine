package ua.ali_x.telegrambot.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.ScheduleService;
import ua.ali_x.telegrambot.service.StatisticService;
import ua.ali_x.telegrambot.service.TelegramService;

@Component
public class StatisticJob implements Job {

    public static StatisticJob instance;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private StatisticService statisticService;
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
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String statistics = instance.statisticService.getStatistics();

        instance.scheduleService.getAllEnabledUser().forEach(schedule -> {
            instance.telegramService.sendStatistic(schedule.getChatId(), statistics, instance.token);
        });

        System.out.println("Job " + this.getClass().getSimpleName() + " is executed");
    }


}