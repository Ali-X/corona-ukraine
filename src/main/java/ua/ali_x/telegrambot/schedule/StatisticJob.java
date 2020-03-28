package ua.ali_x.telegrambot.schedule;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class StatisticJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();

        long chatId = dataMap.getLong("chatId");
        String message = dataMap.getString("message");
        String token = dataMap.getString("token");

        sendStatistic(chatId, message, token);

        System.out.println("Job " + this.getClass().getName() + " is executed");
    }

    public void sendStatistic(long chatId, String message, String token) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(5))
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            UriBuilder builder = UriBuilder
                    .fromUri("https://api.telegram.org")
                    .path("/{token}/sendMessage")
                    .queryParam("chat_id", chatId)
                    .queryParam("text", message);

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(builder.build("bot" + token))
                    .timeout(Duration.ofSeconds(5))
                    .build();

            HttpResponse<String> response = null;


            response = client
                    .send(request, HttpResponse.BodyHandlers.ofString());


            System.out.println(response.statusCode());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}