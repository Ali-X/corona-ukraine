package ua.ali_x.telegrambot.service.course;

import com.jayway.jsonpath.DocumentContext;
import net.minidev.json.JSONArray;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.RequestService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MetalCourse implements CourseService, RequestService {
    private final String api = "https://bank.gov.ua/NBUStatService/v1/statdirectory/exchange?date=%s&json";
    private final String datePattern = "yyyyMMdd";
    private final String gold = "Золото";
    private final String silver = "Срiбло";
    private final String platinum = "Платина";
    private final String paladium = "Паладiй";
    private final String jsonPathPattern = "$.[?(@.txt=='%s')].rate";

    private final String responseToday = "Курс металу станом на сьогодні:";
    private final String responseGold = "\n\n<b>Золото:</b>";
    private final String responseSilver = "\n<b>Срiбло:</b>";
    private final String responsePlatinum = "\n<b>Платина:</b>";
    private final String responsePaladium = "\n<b>Паладiй:</b>";
    private final String responseDiff = "(%s %.2f грн.)";
    private final String responseDiffPlus = "(%s +%.2f грн.)";
    private final String responsePrice = " <b>%.2f</b> грн/ун";
    private final String smileUp = "\uD83D\uDCC8";
    private final String smileDown = "\uD83D\uDCC9";
    private final String responseCurrency = "\n\n<i>1 тр. унція = 31.10348 г.</i>";

    @Override
    public String getCourse() {
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(datePattern);

        LocalDate now = LocalDate.now();
        LocalDate yesterday = now.minusDays(1);

        String dateToday = europeanDateFormatter.format(now);
        String dateYesterday = europeanDateFormatter.format(yesterday);

        DocumentContext documentContextToday = sendGET(String.format(api, dateToday));
        DocumentContext documentContextYesterday = sendGET(String.format(api, dateYesterday));

        Double goldToday = (Double) documentContextToday.read(String.format(jsonPathPattern, gold), JSONArray.class).get(0);
        Double silverToday = (Double) documentContextToday.read(String.format(jsonPathPattern, silver), JSONArray.class).get(0);
        Double platinumToday = (Double) documentContextToday.read(String.format(jsonPathPattern, platinum), JSONArray.class).get(0);
        Double paladiumToday = (Double) documentContextToday.read(String.format(jsonPathPattern, paladium), JSONArray.class).get(0);

        Double goldYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, gold), JSONArray.class).get(0);
        Double silverYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, silver), JSONArray.class).get(0);
        Double platinumYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, platinum), JSONArray.class).get(0);
        Double paladiumYesterday = (Double) documentContextYesterday.read(String.format(jsonPathPattern, paladium), JSONArray.class).get(0);

        Double differenceGold = goldToday - goldYesterday;
        Double differenceSilver = silverToday - silverYesterday;
        Double differencePlatinum = platinumToday - platinumYesterday;
        Double differencePaladium = paladiumToday - paladiumYesterday;

        StringBuilder responseSB = new StringBuilder();
        responseSB.append(responseToday);

//        Gold
        responseSB.append(responseGold);
        responseSB.append(String.format(responsePrice, goldToday));

        if (differenceGold > 0.0d) {
            responseSB.append(String.format(responseDiffPlus, smileUp, differenceGold));
        }

        if (differenceGold < 0.0d) {
            responseSB.append(String.format(responseDiff, smileDown, differenceGold));
        }

        //        Silver
        responseSB.append(responseSilver);
        responseSB.append(String.format(responsePrice, silverToday));

        if (differenceSilver > 0.0d) {
            responseSB.append(String.format(responseDiffPlus, smileUp, differenceSilver));
        }

        if (differenceSilver < 0.0d) {
            responseSB.append(String.format(responseDiff, smileDown, differenceSilver));
        }

        //        Platinum
        responseSB.append(responsePlatinum);
        responseSB.append(String.format(responsePrice, platinumToday));

        if (differencePlatinum > 0.0d) {
            responseSB.append(String.format(responseDiffPlus, smileUp, differencePlatinum));
        }

        if (differencePlatinum < 0.0d) {
            responseSB.append(String.format(responseDiff, smileDown, differencePlatinum));
        }

        //        Paladium
        responseSB.append(responsePaladium);
        responseSB.append(String.format(responsePrice, paladiumToday));

        if (differencePaladium > 0.0d) {
            responseSB.append(String.format(responseDiffPlus, smileUp, differencePaladium));
        }

        if (differencePaladium < 0.0d) {
            responseSB.append(String.format(responseDiff, smileDown, differencePaladium));
        }

        responseSB.append(responseCurrency);

        return responseSB.toString();
    }
}
