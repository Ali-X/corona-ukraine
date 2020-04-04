package ua.ali_x.telegrambot.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class StatisticService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    public String getStatistics() {
        String message = messageTemplateDao.findFirstByCode("statistic").getMessage();

        String basePath = "$.countries.UA";

        String url = "https://www.covidvisualizer.com/api";
        DocumentContext responseJson = sendGET(url);

        Integer recovered = responseJson.read(basePath + ".recovered");
        Integer death = responseJson.read(basePath + ".deaths");
        Integer reports = responseJson.read(basePath + ".reports");

        return String.format(message, reports, recovered, death);
    }

    private DocumentContext sendGET(String url) {
        try {
            URL obj = new URL(url);

            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return JsonPath.parse(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JsonPath.parse("{}");
    }

}
