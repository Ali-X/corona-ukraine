package ua.ali_x.telegrambot.service.statistic;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.service.RequestService;

@Component
public class StatisticJsonUkraineService implements StatisticService, RequestService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    public String getStatistics() {
        return extractStatistic();
    }

    private String extractStatistic() {
        String message = messageTemplateDao.findFirstByCode("statistic_ukraine_d").getMessage();

        String basePath = "$.data[?(@.name_en=='Украина')]";

        String url = "http://coronavirus19.com.ua/ajax/world-stat";
        DocumentContext responseJson = sendGET(url);

        Integer allCases = (Integer) responseJson.read(basePath + ".total_cases", JSONArray.class).get(0);
        Integer recovered = (Integer) responseJson.read(basePath + ".total_recovered", JSONArray.class).get(0);
        Integer death = (Integer) responseJson.read(basePath + ".total_deaths", JSONArray.class).get(0);

        return String.format(message, allCases, recovered, death);
    }
}