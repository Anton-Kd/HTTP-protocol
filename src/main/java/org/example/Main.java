package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static final String LIST_OF_FACTS_ABOUT_CATS = "https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();
        HttpGet request = new HttpGet(LIST_OF_FACTS_ABOUT_CATS);// создание объекта запроса
        request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

        CloseableHttpResponse response = httpClient.execute(request); // отправка запроса
        Arrays.stream(response.getAllHeaders()).forEach(System.out::println); // вывод полученых заголовков

        byte[] bytes = response.getEntity().getContent().readAllBytes();

        String body = new String(bytes, StandardCharsets.UTF_8); // чтение тела ответа
        System.out.println(body);

        // преобразование json в java
        List<Post> posts = mapper.readValue(
                bytes, new TypeReference<>() {
                });
        // фильтруем список голосов
        Stream<Post> voted = posts.stream()
                .filter(value -> value.getUpvotes() != null);
        voted.forEach(System.out::println);
    }
}