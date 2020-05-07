package ua.ali_x.telegrambot.schedule;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageHistoryDao;
import ua.ali_x.telegrambot.model.MessageHistory;
import ua.ali_x.telegrambot.service.QuarantineService;
import ua.ali_x.telegrambot.service.TelegramService;
import ua.ali_x.telegrambot.service.statistic.StatisticService;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    @Autowired
    private MessageHistoryDao messageHistoryDao;

    @Value("${token}")
    private String token;

    public StatisticJob() {
        if (instance == null) {
            instance = this;
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        JobDataMap jobDataMap = jobExecutionContext.getMergedJobDataMap();
        long chatId = jobDataMap.getLong("chatId");
        String daysLeftMsg = instance.quarantineService.getDaysLeftMessage();
        MessageHistory history = instance.messageHistoryDao.findFirstByTypeOrderByDateDesc("statistic");
        String statistics;

        do {
            statistics = instance.statisticHtmlUkraineService.getStatistics();

            if (StringUtils.isEmpty(statistics)) {
                statistics = instance.statisticJsonUkraineService.getStatistics();
            }

            if (history == null || formatter.format(history.getDate()).equals(formatter.format(new Date()))) {
                break;
            } else if (history.getMessage().equals(statistics)) {
                try {
                    Thread.sleep(300000); // 5min
//                    Thread.sleep(5000); // 5 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (!history.getMessage().equals(statistics)) {
                MessageHistory newHistory = new MessageHistory();
                newHistory.setDate(new Date());
                newHistory.setMessage(statistics);
                newHistory.setType("statistic");
                instance.messageHistoryDao.save(newHistory);

                break;
            }
        } while (true);

        instance.telegramService.sendMessage(chatId, statistics, instance.token);
        instance.telegramService.sendMessage(chatId, daysLeftMsg, instance.token);

        System.out.println("Job " + jobDataMap.getString("jobName") + " is executed");
    }
}