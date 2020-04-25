package ua.ali_x.telegrambot.service.statistic;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.service.TranslationService;

import java.util.Arrays;
import java.util.List;

@Component
public class StatisticJsonWorldService implements StatisticService {

    private final List<String> countries = Arrays.asList("Китай", "Италия", "Испания", "Германия", "США", "Польша", "Россия", "ОАЭ", "Египет", "Южная Корея", "Франция", "Япония", "Канада", "Австралия", "Португалия", "Израиль");

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Autowired
    private TranslationService translationService;

    public String getStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        String message = messageTemplateDao.findFirstByCode("statistic_world_d").getMessage();

        String basePath = "$.data";

        String url = "http://coronavirus19.com.ua/ajax/world-stat";
        DocumentContext responseJson = sendGET(url);

        JSONArray jsonArray = responseJson.read(basePath, JSONArray.class);

        jsonArray.forEach(o -> {
            String country = (String) ((JSONArray) JsonPath.read(o, ".name_en")).get(0);

            if (countries.contains(country)) {
                country = translationService.findUkrByRus(country);
                Integer allCases = parseValue(((JSONArray) JsonPath.read(o, ".total_cases")).get(0));
                Integer recovered = parseValue(((JSONArray) JsonPath.read(o, ".total_recovered")).get(0));
                Integer death = parseValue(((JSONArray) JsonPath.read(o, ".total_deaths")).get(0));

                stringBuilder.append(String.format(message, country, allCases, recovered, death));
                stringBuilder.append("\n");
            }
        });

        return stringBuilder.toString();
    }

    private Integer parseValue(Object valueObj) {
        if (valueObj instanceof String) {
            return Integer.valueOf((String) valueObj);
        }

        return (Integer) valueObj;
    }
}