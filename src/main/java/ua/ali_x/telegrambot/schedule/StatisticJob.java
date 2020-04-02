package ua.ali_x.telegrambot.schedule;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ua.ali_x.telegrambot.service.ScheduleService;
import ua.ali_x.telegrambot.service.StatisticService;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class StatisticJob implements Job {

    public static StatisticJob instance;
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private StatisticService statisticService;
    @Value("${token}")
    private String token;

    public StatisticJob() {
        if (instance == null) {
            instance = this;
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String statistics = instance.statisticService.getStatistics();

        instance.scheduleService.getAllEnabledUser().forEach(schedule -> {
            sendStatistic(schedule.getChatId(), statistics, instance.token);
        });

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