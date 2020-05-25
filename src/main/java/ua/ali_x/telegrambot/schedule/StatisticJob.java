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
import ua.ali_x.telegrambot.service.TelegramService;
import ua.ali_x.telegrambot.service.statistic.StatisticService;
import ua.ali_x.telegrambot.utils.DateUtils;

import java.text.SimpleDateFormat;

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
    private MessageHistoryDao messageHistoryDao;
    @Autowired
    private DateUtils dateUtils;

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
        MessageHistory history = instance.messageHistoryDao.findFirstByTypeOrderByDateDesc("statistic");
        String statistics;
        int counter = 15;

        do {
            statistics = instance.statisticHtmlUkraineService.getStatisticsStr();

            if (StringUtils.isEmpty(statistics)) {
                statistics = instance.statisticJsonUkraineService.getStatisticsStr();
            }

            if (history == null) {
                MessageHistory newHistory = new MessageHistory();
                newHistory.setDate(instance.dateUtils.getNow());
                newHistory.setMessage(statistics);
                newHistory.setType("statistic");
                instance.messageHistoryDao.save(newHistory);

                break;
            } else if (formatter.format(history.getDate()).equals(formatter.format(instance.dateUtils.getNow()))) {
                break;
            } else if (history.getMessage().equals(statistics)) {
                counter--;

                try {
                    Thread.sleep(300000); // 5min
//                    Thread.sleep(5000); // 5 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (!history.getMessage().equals(statistics)) {
                MessageHistory newHistory = new MessageHistory();
                newHistory.setDate(instance.dateUtils.getNow());
                newHistory.setMessage(statistics);
                newHistory.setType("statistic");
                instance.messageHistoryDao.save(newHistory);

                break;
            }
        } while (counter > 0);

        instance.telegramService.sendMessage(chatId, statistics, instance.token);

        System.out.println("Job " + jobDataMap.getString("jobName") + " is executed");
    }
}