package ua.ali_x.telegrambot.service.course;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.RequestService;

@Component
public class ExchangeCourseNBU implements CourseService, RequestService {
    private final String api = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?json";
    private final String usd = "USD";
    private final String eur = "EUR";
    private final String rub = "RUB";
    private final String jsonPathPattern = "$.[?(@.cc=='%s')].rate";

    private final String responseToday = "Курс у НБУ станом на сьогодні:";
    private final String responseUsd = "\n\n<b>Долар:</b>";
    private final String responseEur = "\n<b>Євро:</b>";
    private final String responseRub = "\n<b>Рубль:</b>";
    private final String responsePrice = " <b>%.2f</b> грн.";

    @Override
    public String getCourse() {
        DocumentContext documentContextToday = sendGET(api);

        Double usdToday = (Double) documentContextToday.read(String.format(jsonPathPattern, usd), JSONArray.class).get(0);
        Double eurToday = (Double) documentContextToday.read(String.format(jsonPathPattern, eur), JSONArray.class).get(0);
        Double rubToday = (Double) documentContextToday.read(String.format(jsonPathPattern, rub), JSONArray.class).get(0);

        StringBuilder responseSB = new StringBuilder();
        responseSB.append(responseToday);

        //        USD
        responseSB.append(responseUsd);
        responseSB.append(String.format(responsePrice, usdToday));

        //        EUR
        responseSB.append(responseEur);
        responseSB.append(String.format(responsePrice, eurToday));

        //        RUB
        responseSB.append(responseRub);
        responseSB.append(String.format(responsePrice, rubToday));

        return responseSB.toString();
    }
}
