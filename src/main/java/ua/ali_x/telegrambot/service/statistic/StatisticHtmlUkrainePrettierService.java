package ua.ali_x.telegrambot.service.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.dao.StatisticDao;
import ua.ali_x.telegrambot.model.Statistic;

@Component
public class StatisticHtmlUkrainePrettierService implements StatisticService {

    private final String smileUp = "\uD83D\uDD3A";
    private final String smileDown = "\uD83D\uDD3B";
    private final String smileCircle = "\uD83D\uDD34";
    @Autowired
    private StatisticDao statisticDao;
    @Autowired
    private MessageTemplateDao messageTemplateDao;
    @Autowired
    @Qualifier("statisticHtmlUkraineService")
    private StatisticService htmlUkraineStatisticService;

    @Override
    public String getStatisticsStr() {
        String message = messageTemplateDao.findFirstByCode("statistic_ukraine_d_diff").getMessage();
        Statistic statistics = getStatistics();
        Statistic statisticPrev = statisticDao.findFirstByOrderByDateDesc();

        if (statisticPrev == null) {
            return htmlUkraineStatisticService.getStatisticsStr();
        } else if (!statisticPrev.equals(statistics)) {
            return formatMessage(message, statistics, statisticPrev);
        }

        return null;
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

    @Override
    public Statistic getStatistics() {
        return htmlUkraineStatisticService.getStatistics();
    }
}
