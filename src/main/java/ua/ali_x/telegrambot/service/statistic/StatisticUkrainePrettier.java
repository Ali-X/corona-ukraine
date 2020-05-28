package ua.ali_x.telegrambot.service.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageHistoryDao;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.model.Statistic;

@Component
public class StatisticUkrainePrettier {

    private final String smileUp = "\uD83D\uDD3A";
    private final String smileDown = "\uD83D\uDD3B";
    private final String smileCircle = "\uD83D\uDD34";
    @Autowired
    private MessageTemplateDao messageTemplateDao;
    @Autowired
    @Qualifier("statisticHtmlUkraineService")
    private StatisticService htmlUkraineStatisticService;
    @Autowired
    private MessageHistoryDao messageHistoryDao;

    public String beautifyStatistics(Statistic statistics, Statistic statisticPrev) {
        String message = messageTemplateDao.findFirstByCode("statistic_ukraine_d_diff").getMessage();

        if (statisticPrev == null) {
            return htmlUkraineStatisticService.getStatisticsStr();
        } else if (!statisticPrev.equals(statistics)) {
            return formatMessage(message, statistics, statisticPrev);
        } else {
            return messageHistoryDao.findFirstByTypeOrderByDateDesc("statistic").getMessage();
        }
    }

    private String formatMessage(String message, Statistic statistics, Statistic statisticPrev) {
        int infectedDiff = statistics.getInfected() - statisticPrev.getInfected();
        int recoveredDiff = statistics.getRecovered() - statisticPrev.getRecovered();
        int deathsDiff = statistics.getDeaths() - statisticPrev.getDeaths();
        int stillInfected = statistics.getInfected() - statistics.getRecovered() - statistics.getDeaths();
        int stillInfectedPrev = statisticPrev.getInfected() - statisticPrev.getRecovered() - statisticPrev.getDeaths();
        int stillInfectedDiff = stillInfected - stillInfectedPrev;


        return String.format(message,
                statistics.getInfected(),
                getSmile(infectedDiff),
                infectedDiff,

                statistics.getRecovered(),
                getSmile(recoveredDiff),
                recoveredDiff,

                statistics.getDeaths(),
                getSmile(deathsDiff),
                deathsDiff,

                stillInfected,
                getSmile(stillInfectedDiff),
                stillInfectedDiff);
    }

    private String getSmile(int number) {
        return number > 0 ? smileUp : (number < 0 ? smileDown : smileCircle);
    }
}
