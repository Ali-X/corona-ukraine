package ua.ali_x.telegrambot.service.course;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageHistoryDao;
import ua.ali_x.telegrambot.model.MessageHistory;
import ua.ali_x.telegrambot.service.RequestService;

@Component
public class ExchangeCoursePB implements CourseService, RequestService {
    private final String api = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    private final String usd = "USD";
    private final String eur = "EUR";
    private final String rub = "RUR";
    private final String sale = "sale";
    private final String buy = "buy";
    private final String jsonPathPattern = "$.[?(@.ccy=='%s')].%s";

    private final String responseToday = "Курс у Приват Банку станом на сьогодні:";
    private final String responseUsd = "\n\n<b>Долар:</b>";
    private final String responseEur = "\n<b>Євро:</b>";
    private final String responseRub = "\n<b>Рубль:</b>";
    private final String responseBuy = " купівля: <b>%.2f</b> грн.";
    private final String responseSale = " продаж: <b>%.2f</b> грн.";

    @Autowired
    private MessageHistoryDao messageHistoryDao;

    @Override
    public String getCourse() {
        MessageHistory coursePBMessageHistory = messageHistoryDao.findFirstByTypeOrderByDateDesc("coursePB");

        if (coursePBMessageHistory == null || coursePBMessageHistory.getMessage() == null) {
            return extractCourse();
        } else {
            return coursePBMessageHistory.getMessage();
        }
    }

    private String extractCourse() {
        DocumentContext documentContextToday = sendGET(api);

        String usdSaleToday = (String) documentContextToday.read(String.format(jsonPathPattern, usd, sale), JSONArray.class).get(0);
        String usdBuyToday = (String) documentContextToday.read(String.format(jsonPathPattern, usd, buy), JSONArray.class).get(0);

        String eurSaleToday = (String) documentContextToday.read(String.format(jsonPathPattern, eur, sale), JSONArray.class).get(0);
        String eurBuyToday = (String) documentContextToday.read(String.format(jsonPathPattern, eur, buy), JSONArray.class).get(0);

        String rubSaleToday = (String) documentContextToday.read(String.format(jsonPathPattern, rub, sale), JSONArray.class).get(0);
        String rubBuyToday = (String) documentContextToday.read(String.format(jsonPathPattern, rub, buy), JSONArray.class).get(0);

        StringBuilder responseSB = new StringBuilder();
        responseSB.append(responseToday);

        //        USD
        responseSB.append(responseUsd);
        responseSB.append(String.format(responseBuy, Float.parseFloat(usdBuyToday)));
        responseSB.append(String.format(responseSale, Float.parseFloat(usdSaleToday)));

        //        EUR
        responseSB.append(responseEur);
        responseSB.append(String.format(responseBuy, Float.parseFloat(eurBuyToday)));
        responseSB.append(String.format(responseSale, Float.parseFloat(eurSaleToday)));

        //        RUB
        responseSB.append(responseRub);
        responseSB.append(String.format(responseBuy, Float.parseFloat(rubBuyToday)));
        responseSB.append(String.format(responseSale, Float.parseFloat(rubSaleToday)));

        System.out.println(responseSB.toString());

        return responseSB.toString();
    }
}
