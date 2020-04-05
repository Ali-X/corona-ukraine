package ua.ali_x.telegrambot.schedule;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.StatisticHtmlService;
import ua.ali_x.telegrambot.service.StatisticJsonService;
import ua.ali_x.telegrambot.service.TelegramService;

@Component
public class StatisticJob implements Job {

    public static StatisticJob instance;
    @Autowired
    private StatisticJsonService statisticJsonService;
    @Autowired
    private StatisticHtmlService statisticHtmlService;
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
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String statistics = instance.statisticHtmlService.getStatistics();

        if (StringUtils.isEmpty(statistics)) {
            statistics = instance.statisticJsonService.getStatistics();
        }

        instance.telegramService.sendStatistic(jobDataMap.getLong("chatId"), statistics, instance.token);

        System.out.println("Job " + jobDataMap.getString("jobName") + " is executed");
    }
}