package ua.ali_x.telegrambot.service.course;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.RequestService;

@Component
public class BitcoinCoursePB implements CourseService, RequestService {
    private final String api = "https://api.privatbank.ua/p24api/pubinfo?exchange&json&coursid=11";
    private final String btc = "BTC";
    private final String sale = "sale";
    private final String buy = "buy";
    private final String jsonPathPattern = "$.[?(@.ccy=='%s')].%s";

    private final String responseToday = "Курс біткойна у Приват Банку станом на сьогодні:";
    private final String responseBtc = "\n\n<b>BTC:</b>";
    private final String responseBuy = " купівля: <b>%s</b> USD";
    private final String responseSale = " продаж: <b>%s</b> USD";


    @Override
    public String getCourse() {
        DocumentContext documentContextToday = sendGET(api);

        String btcSaleToday = (String) documentContextToday.read(String.format(jsonPathPattern, btc, sale), JSONArray.class).get(0);
        String btcBuyToday = (String) documentContextToday.read(String.format(jsonPathPattern, btc, buy), JSONArray.class).get(0);

        StringBuilder responseSB = new StringBuilder();
        responseSB.append(responseToday);

//        BTC
        responseSB.append(responseBtc);
        responseSB.append(String.format(responseBuy, btcSaleToday));
        responseSB.append(String.format(responseSale, btcBuyToday));

        System.out.println(responseSB.toString());

        return responseSB.toString();
    }
}
