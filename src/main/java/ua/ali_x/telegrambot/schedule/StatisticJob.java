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
import ua.ali_x.telegrambot.dao.StatisticDao;
import ua.ali_x.telegrambot.model.MessageHistory;
import ua.ali_x.telegrambot.model.Statistic;
import ua.ali_x.telegrambot.service.TelegramService;
import ua.ali_x.telegrambot.service.statistic.StatisticService;
import ua.ali_x.telegrambot.service.statistic.StatisticUkrainePrettier;
import ua.ali_x.telegrambot.utils.DateUtils;

import java.text.SimpleDateFormat;

@Component
public class StatisticJob implements Job {

    public static StatisticJob instance;
    @Autowired
    @Qualifier("statisticHtmlUkraineService")
    private StatisticService statisticHtmlUkraineService;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private MessageHistoryDao messageHistoryDao;
    @Autowired
    private DateUtils dateUtils;
    @Autowired
    private StatisticDao statisticDao;
    @Autowired
    private StatisticUkrainePrettier statisticUkrainePrettier;

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
        String statisticsStr = StringUtils.EMPTY;
        int counter = 15;

        do {
            Statistic statistics = instance.statisticHtmlUkraineService.getStatistics();
            Statistic statisticPrev = instance.statisticDao.findFirstByOrderByDateDesc();

            if (statisticPrev != null && formatter.format(statisticPrev.getDate()).equals(formatter.format(instance.dateUtils.getNow()))) {
                statisticsStr = instance.messageHistoryDao.findFirstByTypeOrderByDateDesc("statistic").getMessage();
                break;
            } else if (!statistics.equals(statisticPrev)) {
                statisticsStr = instance.statisticUkrainePrettier.beautifyStatistics(statistics, statisticPrev);

                MessageHistory newHistory = new MessageHistory();
                newHistory.setDate(instance.dateUtils.getNow());
                newHistory.setMessage(statisticsStr);
                newHistory.setType("statistic");

                instance.messageHistoryDao.save(newHistory);
                instance.statisticDao.save(statistics);
                break;
            } else {
                counter--;

                try {
                    Thread.sleep(300000); // 5min
//                    Thread.sleep(5000); // 5 sec
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while (counter > 0);

        instance.telegramService.sendMessage(chatId, statisticsStr, instance.token);

        System.out.println("Job " + jobDataMap.getString("jobName") + " is executed");
    }
}