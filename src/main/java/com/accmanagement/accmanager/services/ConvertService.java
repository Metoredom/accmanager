package com.accmanagement.accmanager.services;

import com.accmanagement.accmanager.exceptions.ServiceUnavailableException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static com.accmanagement.accmanager.configuration.LinksConfig.CONVERT_API_URI;

@Service
public class ConvertService {

    public Mono<JsonNode> convert(String ccy_from, String ccy_to, Double amount) {
        WebClient webClient = WebClient.builder().build();

        return webClient
                .get()
                .uri(String.format(CONVERT_API_URI, ccy_to, ccy_from, amount))
                .header("apikey", "dtb9YXJOCHvpjcdkZxsBn4V6Zecnohvz")
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new ServiceUnavailableException()))
                .bodyToMono(JsonNode.class);
    }

}
