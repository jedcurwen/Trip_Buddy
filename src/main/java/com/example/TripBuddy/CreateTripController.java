package com.example.TripBuddy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Controller
public class CreateTripController {
	private String weatherDescription;
	private String weatherTemp;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
    private GenerateID generateID;
	
	@GetMapping("/createTrip")
	public String createTrip(@CookieValue(value="username")String username, Model model) {						
		return "createTrip";
	}
	
	@PostMapping("/show")
	public String createTrip(
            @RequestParam(name = "location") String location,
            @RequestParam(name = "date")String tripDate,
            @RequestParam(name = "duration")Integer tripDuration,
            @CookieValue(value="username")String username,
            Model model) throws IOException, InterruptedException {

        model.addAttribute("date", tripDate);

        String nominatimApiUrl = "https://nominatim.openstreetmap.org/search?format=json&q=" + location;
        HttpClient nominatimClient = HttpClient.newHttpClient();
        HttpRequest nominatimRequest = HttpRequest.newBuilder().uri(URI.create(nominatimApiUrl)).build();

        HttpResponse<String> nominatimResponse = nominatimClient.send(nominatimRequest, HttpResponse.BodyHandlers.ofString());
        String nominatimResponseBody = nominatimResponse.body();
        JSONArray resultsArray = new JSONArray(nominatimResponseBody);
        if (!resultsArray.isEmpty()) {
            JSONObject location2 = resultsArray.getJSONObject(0);

            double latitude = location2.getDouble("lat");
            double longitude = location2.getDouble("lon");
            
            model.addAttribute("location",latitude + ","+ longitude );
            model.addAttribute("duration", tripDuration);
           
            //Weather API
            String apiUrl = "https://api.worldweatheronline.com/premium/v1/weather.ashx?key=166637326fd04370a19175742232211" +
                    "&q=" + latitude + "," + longitude +  //Use "Long & Lat" to get weather location 
                    "&format=json&num_of_days=7&date="+tripDate; //Set date as TripDate from HTML form.
            
            try{
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(apiUrl)).build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
              //This will create a new request to our API
                
                if (response.statusCode() == 200) {
                    String responseBody = response.body();
                    
                    
                    JSONObject root = new JSONObject(responseBody);
                    JSONArray weatherArray = root.getJSONObject("data").getJSONArray("weather");
                    

        // Extract and display the relevant weather information
                    for (int i = 0; i < weatherArray.length(); i++) {
                        JSONObject dayWeather = weatherArray.getJSONObject(i);
                        weatherDescription = dayWeather.getJSONArray("hourly").getJSONObject(0).getJSONArray("weatherDesc").getJSONObject(0).getString("value");

                        JSONArray cityTemp = dayWeather.getJSONArray("hourly");
                        model.addAttribute("weatherData", dayWeather);
                        
                        if(cityTemp.length()>0){
                            JSONObject temp = cityTemp.getJSONObject(0);
                            if(temp.has("tempC")){
                            	weatherTemp = temp.getString("tempC");                           	
                            	model.addAttribute("temperature", temp.getString("tempC"));                                
                            }else{
                            	model.addAttribute("temperature", "Temp not found.");
                            }
                        }
                        
                    }
                } else {
                        System.err.println("API request failed with status code: " + response.statusCode());
                        
                    }
                }catch(IOException | InterruptedException | JSONException e)
            { 
                	
            }                                   
            String locationWeather = weatherTemp +"°C" +" " + weatherDescription;

            String tripID = generateID.getJson();
            model.addAttribute("tripID", tripID);
            String sql = "SELECT UserID FROM User WHERE Username =?";
            String userID = jdbcTemplate.queryForObject(sql, String.class, username);                                   
            
    }           
        return "createTrip";
        }
	
	@PostMapping("/newTrip")
	public String newTrip(
	        @RequestParam(name = "tripID") String tripID,
	        @RequestParam(name = "date") String date,
	        @RequestParam(name = "location") String location,
	        @RequestParam(name = "weatherDescription") String weatherDescription,
	        @RequestParam(name = "temperature") String temperature,
	        @RequestParam(name = "duration") String duration,
	        @CookieValue(value = "username") String username,
	        Model model) {
		String getUser = "SELECT UserID FROM User WHERE Username=?";
		String userID = jdbcTemplate.queryForObject(getUser, String.class,username);
		
		String coordinates = location;
	    
	    String[] longLat = coordinates.split(",");
	    
	    if(longLat.length ==2) {
	    	String latitude = longLat[0];
	    	String longitude = longLat[1];	   
	    		    	
	    	String nominatimApiUrl = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + latitude + "&lon=" + longitude;
	    	
	    	HttpClient client = HttpClient.newHttpClient();

	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(nominatimApiUrl))
	                .build();

	        try {
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            if (response.statusCode() == 200) {
	                String responseBody = response.body();
	                
	                
	                System.out.println(responseBody);
	                JSONObject json = new JSONObject(responseBody);

	                String town = json.getJSONObject("address").getString("city");
	                	                	                	                
	                String country = json.getJSONObject("address").getString("country");
	                String countryCode = json.getJSONObject("address").getString("country_code");
	                String destination = town+","+country;
	                
	                String weather = weatherDescription+", "+ weatherTemp+"°C";
	                
	               jdbcTemplate.update("INSERT INTO Trip (TripID,UserID,TripDate,TripDestination,Country_Code,TripWeather,TripDuration,TripInterest)VALUES(?,?,?,?,?,?,?,0)"
	                		,tripID,userID,date,destination,countryCode,weather,duration);
	               
	               
	               model.addAttribute("tripCreated", "Trip has been created!");
	               return "createTrip";


	            } else {
	                System.err.println("Reverse geocoding request failed with status code: " + response.statusCode());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    	
	    }
	    
	    return "createTrip";
	}

        
	}



