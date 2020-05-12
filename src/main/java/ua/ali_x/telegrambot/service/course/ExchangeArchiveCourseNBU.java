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

    private final String responseToday = "Курс у НБУ станом на сьогодні:";
    private final String responseUsd = "\n\n<b>Долар:</b>";
    private final String responseEur = "\n<b>Євро:</b>";
    private final String responseRub = "\n<b>Рубль:</b>";
    private final String responseByu = " купівля: <b>%.2f</b> грн.";
    private final String responseSale = " продаж: <b>%.2f</b> грн.";

    @Override
    public String getCourse() {
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDate now = LocalDate.now();

        String dateToday = europeanDateFormatter.format(now);

        DocumentContext documentContextToday = sendGET(api + dateToday);

        Double usdSaleToday = (Double) documentContextToday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).get(0);
        Double usdBuyToday = (Double) documentContextToday.read(String.format(jsonPathPattern, usd, buy), JSONArray.class).get(0);

        Double eurSaleToday = (Double) documentContextToday.read(String.format(jsonPathPattern, eur, sale), JSONArray.class).get(0);
        Double eurBuyToday = (Double) documentContextToday.read(String.format(jsonPathPattern, eur, buy), JSONArray.class).get(0);

        Double rubSaleToday = (Double) documentContextToday.read(String.format(jsonPathPattern, rub, sale), JSONArray.class).get(0);
        Double rubBuyToday = (Double) documentContextToday.read(String.format(jsonPathPattern, rub, buy), JSONArray.class).get(0);

        StringBuilder responseSB = new StringBuilder();
        responseSB.append(responseToday);

//        USD
        responseSB.append(responseUsd);
        responseSB.append(String.format(responseByu, usdSaleToday));
        responseSB.append(String.format(responseSale, usdBuyToday));

//        EUR
        responseSB.append(responseEur);
        responseSB.append(String.format(responseByu, eurSaleToday));
        responseSB.append(String.format(responseSale, eurBuyToday));

        //        RUB
        responseSB.append(responseRub);
        responseSB.append(String.format(responseByu, rubSaleToday));
        responseSB.append(String.format(responseSale, rubBuyToday));

        return responseSB.toString();
    }
}
