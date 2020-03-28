package ua.ali_x.telegrambot.service;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.dao.MessageTemplateDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class StatisticService {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    public String getStatistics() {
        String message = messageTemplateDao.findFirstByCode("statistic").getMessage();

        String dateNow = new SimpleDateFormat("M/dd/yy").format(new Date());
        String dateYesterday = new SimpleDateFormat("M/dd/yy").format(yesterday());
        String datesPathTemplate = "$.data[?(@.['Country/Region']=='Ukraine')].dates[?(@.date=='%s')]";
        String datesPath;

        String url = "https://api.the2019ncov.com/api/cases";
        JSONObject responseJson = sendGET(url);
        String responseString = responseJson.toString();

        datesPath = String.format(datesPathTemplate, dateNow);

        if (((JSONArray) JsonPath.parse(responseString).read(datesPath)).isEmpty()) {
            datesPath = String.format(datesPathTemplate, dateYesterday);
        }

        String dateStr = (String) ((JSONArray) JsonPath.parse(responseString).read(datesPath + ".date")).get(0);
        Integer recovered = (Integer) ((JSONArray) JsonPath.parse(responseString).read(datesPath + ".recovered")).get(0);
        Integer death = (Integer) ((JSONArray) JsonPath.parse(responseString).read(datesPath + ".death")).get(0);
        Integer confirmed = (Integer) ((JSONArray) JsonPath.parse(responseString).read(datesPath + ".confirmed")).get(0);

        return String.format(message, dateStr, confirmed, recovered, death);
    }

    private Date yesterday() {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }


    private JSONObject sendGET(String url) {
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

                return new JSONObject(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONObject("{}");
    }

}
