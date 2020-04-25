package ua.ali_x.telegrambot.service.statistic;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class StatisticJsonUkraineService implements StatisticService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    public String getStatistics() {
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
