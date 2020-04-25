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
public class StatisticJsonUkraineRegionService implements StatisticService {

    private final List<String> excludedRegions = Arrays.asList("АР Крым");

    @Autowired
    private TranslationService translationService;

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    public String getStatistics() {
        StringBuilder stringBuilder = new StringBuilder();
        String message = messageTemplateDao.findFirstByCode("statistic_region_d").getMessage();

        String basePath = "$.data";

        String url = "http://coronavirus19.com.ua/ajax/ukraine-stat";
        DocumentContext responseJson = sendGET(url);

        JSONArray jsonArray = responseJson.read(basePath, JSONArray.class);

        jsonArray.forEach(o -> {
            String region = (String) ((JSONArray) JsonPath.read(o, ".name_en")).get(0);

            if (!excludedRegions.contains(region)) {
                region = translationService.findUkrByRus(region);
                Integer allCases = parseValue(((JSONArray) JsonPath.read(o, ".total_cases")).get(0));

                stringBuilder.append(String.format(message, region, allCases));
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
