package ua.ali_x.telegrambot.schedule;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.QuarantineService;
import ua.ali_x.telegrambot.service.TelegramService;
import ua.ali_x.telegrambot.service.statistic.StatisticService;

@Component
public class StatisticJob implements Job {

    public static StatisticJob instance;
    @Autowired
    @Qualifier("statisticJsonUkraineService")
    private StatisticService statisticJsonUkraineService;
    @Autowired
    @Qualifier("statisticHtmlUkraineService")
    private StatisticService statisticHtmlUkraineService;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private QuarantineService quarantineService;

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
        long chatId = jobDataMap.getLong("chatId");
        String daysLeftMsg = instance.quarantineService.getDaysLeftMessage();
        String statistics = instance.statisticHtmlUkraineService.getStatistics();

        if (StringUtils.isEmpty(statistics)) {
            statistics = instance.statisticJsonUkraineService.getStatistics();
        }

        instance.telegramService.sendMessage(chatId, statistics, instance.token);
        instance.telegramService.sendMessage(chatId, daysLeftMsg, instance.token);

        System.out.println("Job " + jobDataMap.getString("jobName") + " is executed");
    }
}