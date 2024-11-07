package com.example.TripBuddy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/ExchangeRate")
public class ExchangeRate {

    private static final String CURRENCY_LAYER_API_URL = "http://apilayer.net/api/live";
    private static final String API_KEY = "c593d7bbca65b9b995f8ab4d0bc915ae";
    
    private String source;
    private String target;

    @GetMapping
    public String getExchangeRate(String sourceCurrency,String targetCurrency)
            throws IOException, InterruptedException {
    	
    	
    	
    	source = sourceCurrency;
    	target=targetCurrency;
    		 	
        
        String apiUrlWithCurrencies = String.format("%s?access_key=%s&currencies=%s&source=%s&format=1",
                CURRENCY_LAYER_API_URL, API_KEY, targetCurrency, sourceCurrency);

        HttpClient httpClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrlWithCurrencies))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse the JSON response
        String exchangeRate = parseExchangeRateResponse(response.body());
        
        return exchangeRate;
    }

    private String parseExchangeRateResponse(String responseBody) throws IOException {

    	
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // Check if the response indicates success
        boolean success = jsonNode.path("success").asBoolean();
        if (!success) {
           
            String errorInfo = jsonNode.path("error").path("info").asText();
            return "Error: " + errorInfo;
        }

        // Extract exchange rate information
        String sourceCurrency = jsonNode.path("source").asText();
        JsonNode quotesNode = jsonNode.path("quotes");

        
        double rate = quotesNode.path(source+target).asDouble();
        
        String exchangeRateInfo = String.format("Exchange rate for %s to %s is %.4f", source, target, rate);

        return exchangeRateInfo;
    }
}

