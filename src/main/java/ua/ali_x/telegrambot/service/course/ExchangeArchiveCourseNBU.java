package ua.ali_x.telegrambot.service.course;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.RequestService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ExchangeArchiveCourseNBU implements CourseService, RequestService {

    private final String api = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    private final String datePattern = "dd.MM.yyyy";
    private final String usd = "USD";
    private final String eur = "EUR";
    private final String rub = "RUB";
    private final String sale = "saleRateNB";
    private final String buy = "purchaseRateNB";
    private final String jsonPathPattern = "$.exchangeRate[?(@.currency=='%s')].%s";
    private final String responseToday = "Курс у НБУ станом на %s:";
    private final String responseUsd = "\n\n<b>Долар:</b>";
    private final String responseEur = "\n<b>Євро:</b>";
    private final String responseRub = "\n<b>Рубль:</b>";
    private final String responseByu = " купівля: <b>%.2f</b> грн.";
    private final String responseSale = " продаж: <b>%.2f</b> грн.";

    @Override
    public String getCourse() {
        return extractCourse();
    }

    private String extractCourse() {
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(datePattern);

        int n = 0;

        do {
            LocalDate date = LocalDate.now().minusDays(n);
            String dateStr = europeanDateFormatter.format(date);

            DocumentContext documentContextToday = sendGET(api + dateStr);

            if (!documentContextToday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).isEmpty()) {
                Double usdSale = (Double) documentContextToday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).get(0);
                Double usdBuy = (Double) documentContextToday.read(String.format(jsonPathPattern, usd, buy), JSONArray.class).get(0);

                Double eurSale = (Double) documentContextToday.read(String.format(jsonPathPattern, eur, sale), JSONArray.class).get(0);
                Double eurBuy = (Double) documentContextToday.read(String.format(jsonPathPattern, eur, buy), JSONArray.class).get(0);

                Double rubSale = (Double) documentContextToday.read(String.format(jsonPathPattern, rub, sale), JSONArray.class).get(0);
                Double rubBuy = (Double) documentContextToday.read(String.format(jsonPathPattern, rub, buy), JSONArray.class).get(0);

                StringBuilder responseSB = new StringBuilder();
                responseSB.append(String.format(responseToday, dateStr));

//        USD
                responseSB.append(responseUsd);
                responseSB.append(String.format(responseByu, usdSale));
                responseSB.append(String.format(responseSale, usdBuy));

//        EUR
                responseSB.append(responseEur);
                responseSB.append(String.format(responseByu, eurSale));
                responseSB.append(String.format(responseSale, eurBuy));

                //        RUB
                responseSB.append(responseRub);
                responseSB.append(String.format(responseByu, rubSale));
                responseSB.append(String.format(responseSale, rubBuy));

                return responseSB.toString();
            }

            n++;
        } while (n < 10);

        return "Вибачте, наразi даннi не доступнi.";
    }
}
