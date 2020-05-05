package ua.ali_x.telegrambot.service.course;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.RequestService;

import java.io.IOException;

@Component
public class OilCourse implements CourseService, RequestService {
    private final String api = "https://www.finanz.ru/birzhevyye-tovary/grafik-v-realnom-vremeni/neft-cena";
    private final String responseToday = "Курс нафти Brent станом на сьогодні:";
    private final String responsePrice = " <b>%s</b> USD/барель";

    @Override
    public String getCourse() {
        try {
            String basePath = ".realtime_quote_price .push-data";

            Document doc = Jsoup.connect(api).get();

            String price = doc.select(basePath).get(0).ownText();

            StringBuilder responseSB = new StringBuilder();

            responseSB.append(responseToday);
            responseSB.append(String.format(responsePrice, price));

            return responseSB.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
