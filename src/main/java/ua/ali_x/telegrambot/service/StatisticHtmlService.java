package ua.ali_x.telegrambot.service;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;

import java.io.IOException;

@Component
public class StatisticHtmlService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    public String getStatistics() {
        try {
            String message = messageTemplateDao.findFirstByCode("statistic").getMessage();

            String recovered = "";
            String death = "";
            String infected = "";

            String basePath = "#content .fields .one-field";

            String url = "https://covid19.com.ua/";
            Document doc = Jsoup.connect(url).get();

            Elements elements = doc.select(basePath);

            for (Element element : elements) {
                Elements labelElement = element.select(".field-label");
                Elements valueElement = element.select(".field-value");

                if (!labelElement.isEmpty() && !valueElement.isEmpty()) {
                    switch (labelElement.get(0).ownText()) {
                        case "хворих на Covid-19":
                            infected = valueElement.get(0).ownText();
                            break;
                        case "одужало":
                            recovered = valueElement.get(0).ownText();
                            break;
                        case "летальних випадків":
                            death = valueElement.get(0).ownText();
                            break;
                    }
                }
            }

            if (StringUtils.isEmpty(recovered)
                    || StringUtils.isEmpty(death)
                    || StringUtils.isEmpty(infected)) {
                return StringUtils.EMPTY;
            }

            return String.format(message, infected, recovered, death);
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return StringUtils.EMPTY;
        }
    }
}