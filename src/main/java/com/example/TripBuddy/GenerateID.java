package com.example.TripBuddy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/GenerateID")
public class GenerateID {

    private static final String RANDOM_ORG_API_URL = "https://api.random.org/json-rpc/2/invoke";
    private static final String API_KEY = "b2d5abde-1415-4ea7-bfd5-a1ce968e92f1";

    @GetMapping
    public String getJson() {
    	
        try {
            String jsonPayload = String.format(
                    "{\"jsonrpc\":\"2.0\",\"method\":\"generateStrings\",\"params\":{\"apiKey\":\"%s\",\"n\":1,\"length\":8,\"characters\":\"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz\",\"replacement\":true},\"id\":1}",
                    API_KEY);

            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RANDOM_ORG_API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response
            String data = parseRandomOrgResponse(response.body());

            return parseRandomOrgResponse(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace(); 
            return "Internal Server Error";
        }
    }

    private String parseRandomOrgResponse(String responseBody) throws IOException {
    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	JsonNode jsonNode = objectMapper.readTree(responseBody);
    	
    	JsonNode dataNode = jsonNode.path("result").path("random").path("data");
    	String data = dataNode.isArray() ? dataNode.get(0).asText() : null;
    	

        return data; 
    }
}

