package ua.ali_x.telegrambot.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface RequestService {

    default DocumentContext sendGET(String url) {
        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("GET Response Code :: " + response.statusCode() + " :: " + url);

            if (response.statusCode() == HttpURLConnection.HTTP_OK) { // success
                return JsonPath.parse(response.body());
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return JsonPath.parse("{}");
    }
}
