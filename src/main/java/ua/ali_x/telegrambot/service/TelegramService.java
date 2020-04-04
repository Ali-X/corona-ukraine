package ua.ali_x.telegrambot.service;

import org.springframework.stereotype.Component;

import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class TelegramService {

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
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
