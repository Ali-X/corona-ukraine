package ua.ali_x.telegrambot.service.course;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.RequestService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class ExchangeCoursePB implements CourseService, RequestService {
    private final String api = "https://api.privatbank.ua/p24api/exchange_rates?json&date=";
    private final String datePattern = "dd.MM.yyyy";
    private final String usd = "USD";
    private final String eur = "EUR";
    private final String rub = "RUB";
    private final String sale = "saleRate";
    private final String buy = "purchaseRate";
    private final String jsonPathPattern = "$.exchangeRate[?(@.currency=='%s')].%s";

    private final String responseToday = "Курс у Приват Банку станом на сьогодні:";
    private final String responseUsd = "\n\n<b>Долар:</b>";
    private final String responseEur = "\n<b>Євро:</b>";
    private final String responseRub = "\n<b>Рубль:</b>";
    private final String responseDiff = "(%s %.2f грн.)";
    private final String responseDiffPlus = "(%s +%.2f грн.)";
    private final String responseByu = " купівля: <b>%.2f</b> грн.";
    private final String responseSale = " продаж: <b>%.2f</b> грн.";
    private final String smileUp = "\uD83D\uDCC8";
    private final String smileDown = "\uD83D\uDCC9";


    @Override
    public String getCourse() {
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        String dateToday = europeanDateFormatter.format(now);
        String dateYesterday = europeanDateFormatter.format(yesterday);


        DocumentContext documentContextToday = sendGET(api + dateToday);
        DocumentContext documentContextYesterday = sendGET(api + dateYesterday);

        if (documentContextToday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).isEmpty()) {
            Double usdSaleYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).get(0);
            Double usdBuyYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, usd, buy), JSONArray.class).get(0);

            Double eurSaleYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, eur, sale), JSONArray.class).get(0);
            Double eurBuyYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, eur, buy), JSONArray.class).get(0);

            Double rubSaleYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, rub, sale), JSONArray.class).get(0);
            Double rubBuyYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, rub, buy), JSONArray.class).get(0);

            StringBuilder responseSB = new StringBuilder();
            responseSB.append(responseToday);

//        USD
            responseSB.append(responseUsd);
            responseSB.append(String.format(responseByu, usdSaleYesterday));
            responseSB.append(String.format(responseSale, usdBuyYesterday));

//        EUR
            responseSB.append(responseEur);
            responseSB.append(String.format(responseByu, eurSaleYesterday));
            responseSB.append(String.format(responseSale, eurBuyYesterday));

            //        RUB
            responseSB.append(responseRub);
            responseSB.append(String.format(responseByu, rubSaleYesterday));
            responseSB.append(String.format(responseSale, rubBuyYesterday));

            return responseSB.toString();
        } else {
            Double usdSaleToday = (Double) documentContextToday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).get(0);
            Double usdBuyToday = (Double) documentContextToday.read(String.format(jsonPathPattern, usd, buy), JSONArray.class).get(0);

            Double eurSaleToday = (Double) documentContextToday.read(String.format(jsonPathPattern, eur, sale), JSONArray.class).get(0);
            Double eurBuyToday = (Double) documentContextToday.read(String.format(jsonPathPattern, eur, buy), JSONArray.class).get(0);

            Double rubSaleToday = (Double) documentContextToday.read(String.format(jsonPathPattern, rub, sale), JSONArray.class).get(0);
            Double rubBuyToday = (Double) documentContextToday.read(String.format(jsonPathPattern, rub, buy), JSONArray.class).get(0);

            Double usdSaleYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).get(0);
            Double usdBuyYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, usd, buy), JSONArray.class).get(0);

            Double eurSaleYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, eur, sale), JSONArray.class).get(0);
            Double eurBuyYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, eur, buy), JSONArray.class).get(0);

            Double rubSaleYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, rub, sale), JSONArray.class).get(0);
            Double rubBuyYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, rub, buy), JSONArray.class).get(0);

            Double differenceUsdSale = usdSaleToday - usdSaleYesterday;
            Double differenceUsdBuy = usdBuyToday - usdBuyYesterday;

            Double differenceEurSale = eurSaleToday - eurSaleYesterday;
            Double differenceEurBuy = eurBuyToday - eurBuyYesterday;

            Double differenceRubSale = rubSaleToday - rubSaleYesterday;
            Double differenceRubBuy = rubBuyToday - rubBuyYesterday;

            StringBuilder responseSB = new StringBuilder();
            responseSB.append(responseToday);

//        USD
            responseSB.append(responseUsd);
            responseSB.append(String.format(responseByu, usdBuyToday));

            if (differenceUsdBuy > 0.0d) {
                responseSB.append(String.format(responseDiffPlus, smileUp, differenceUsdBuy));
            }

            if (differenceUsdBuy < 0.0d) {
                responseSB.append(String.format(responseDiff, smileDown, differenceUsdBuy));
            }

            responseSB.append(String.format(responseSale, usdSaleToday));

            if (differenceUsdSale > 0.0d) {
                responseSB.append(String.format(responseDiffPlus, smileUp, differenceUsdSale));
            }

            if (differenceUsdSale < 0.0d) {
                responseSB.append(String.format(responseDiff, smileDown, differenceUsdSale));
            }

//        EUR
            responseSB.append(responseEur);
            responseSB.append(String.format(responseByu, eurBuyToday));

            if (differenceEurBuy > 0.0d) {
                responseSB.append(String.format(responseDiffPlus, smileUp, differenceEurBuy));
            }

            if (differenceEurBuy > 0.0d) {
                responseSB.append(String.format(responseDiff, smileDown, differenceEurBuy));
            }

            responseSB.append(String.format(responseSale, eurSaleToday));

            if (differenceEurSale > 0.0d) {
                responseSB.append(String.format(responseDiffPlus, smileUp, differenceEurSale));
            }

            if (differenceEurSale < 0.0d) {
                responseSB.append(String.format(responseDiff, smileDown, differenceEurSale));
            }

            //        RUB
            responseSB.append(responseRub);
            responseSB.append(String.format(responseByu, rubBuyToday));

            if (differenceRubBuy > 0.0d) {
                responseSB.append(String.format(responseDiffPlus, smileUp, differenceRubBuy));
            }

            if (differenceRubBuy < 0.0d) {
                responseSB.append(String.format(responseDiff, smileDown, differenceRubBuy));
            }

            responseSB.append(String.format(responseSale, rubSaleToday));

            if (differenceRubSale > 0.0d) {
                responseSB.append(String.format(responseDiffPlus, smileUp, differenceRubSale));
            }

            if (differenceRubSale < 0.0d) {
                responseSB.append(String.format(responseDiff, smileDown, differenceRubSale));
            }

            return responseSB.toString();
        }
    }
}
