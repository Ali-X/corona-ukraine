package ua.ali_x.telegrambot.service.statistic;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;
import ua.ali_x.telegrambot.service.TranslationService;

import java.math.BigDecimal;
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
        String messageAll = messageTemplateDao.findFirstByCode("statistic_all_world_s").getMessage();

        String basePath = "$.data";

        String url = "http://coronavirus19.com.ua/ajax/world-stat";
        DocumentContext responseJson = sendGET(url);

        JSONArray jsonArray = responseJson.read(basePath, JSONArray.class);

        BigDecimal totalCases = new BigDecimal(0);
        BigDecimal totalRecovered = new BigDecimal(0);
        BigDecimal totalDeath = new BigDecimal(0);

        for (Object o : jsonArray) {
            String country = (String) ((JSONArray) JsonPath.read(o, ".name_en")).get(0);

            Integer allCases = parseValue(((JSONArray) JsonPath.read(o, ".total_cases")).get(0));
            Integer recovered = parseValue(((JSONArray) JsonPath.read(o, ".total_recovered")).get(0));
            Integer death = parseValue(((JSONArray) JsonPath.read(o, ".total_deaths")).get(0));

            totalCases = totalCases.add(new BigDecimal(allCases));
            totalRecovered = totalRecovered.add(new BigDecimal(recovered));
            totalDeath = totalDeath.add(new BigDecimal(death));

            if (countries.contains(country)) {
                country = translationService.findUkrByRus(country);
                stringBuilder.append(String.format(message, country, allCases, recovered, death));
                stringBuilder.append("\n");
            }
        }

        stringBuilder.append("\n");
        stringBuilder.append(String.format(messageAll, totalCases.toString(), totalRecovered.toString(), totalDeath.toString()));

        return stringBuilder.toString();
    }

    private Integer parseValue(Object valueObj) {
        if (valueObj instanceof String) {
            return Integer.valueOf((String) valueObj);
        }

        return (Integer) valueObj;
    }
}