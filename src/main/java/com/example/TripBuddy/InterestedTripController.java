package com.example.TripBuddy;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

@RequestMapping("/interestedTrips")
public class InterestedTripController {
	
	@Autowired
    public ExchangeRate exchangeRate; 
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/interestedTrips")
	public String myInterestedTrips(@CookieValue(value="username")String username, Model model) {
		
		//Get UserID
		String sql = "SELECT UserID from User WHERE Username =?";
		String userID = jdbcTemplate.queryForObject(sql, String.class, username);
	
		
		String sql2 = "SELECT COUNT(*) FROM User_Trip WHERE UserID=?";
		Integer tripCount = jdbcTemplate.queryForObject(sql2, Integer.class, userID);
		
		model.addAttribute("error", "Number of trips you have declared an interest in: " + tripCount);
		//Return trips from User_Trip table where User has declared interest in trip(s)
		
		String getTrip = "SELECT TripID, TripDate, Trip_Destination, TripDuration FROM User_Trip WHERE UserID=?";

		
		List<Map<String, Object>> getTripsResults = jdbcTemplate.queryForList(getTrip, userID);
		
		if(!getTripsResults.isEmpty()) {
			for (Map<String, Object> trips : getTripsResults) {
				String tripId = (String) trips.get("TripID");
				String tripDate = (String) trips.get("TripDate");
				String tripDestination = (String) trips.get("Trip_Destination");
				Integer tripDuration = (Integer)trips.get("TripDuration");
				//Add results to Thymeleaf.
				model.addAttribute("trips", getTripsResults);
		}
			
		}else {
		
		}
		
		return "myInterestedTrips";
	}
	
	@PostMapping("/exchangeRate")
	public String exchangeRate(@CookieValue(value="username")String username,
			@RequestParam(name="tripID")String tripID,
			Model model) throws IOException, InterruptedException {	
		//Get UserID
				String sql = "SELECT UserID from User WHERE Username =?";
				String userID = jdbcTemplate.queryForObject(sql, String.class, username);
			
				
				String sql2 = "SELECT COUNT(*) FROM User_Trip WHERE UserID=?";
				Integer tripCount = jdbcTemplate.queryForObject(sql2, Integer.class, userID);
				
				model.addAttribute("error", "Number of trips you have declared an interest in: " + tripCount);
				//Return trips from User_Trip table where User has declared interest in trip(s)
				
				String getTrip = "SELECT TripID, TripDate, Trip_Destination, TripDuration FROM User_Trip WHERE UserID=?";

				
				List<Map<String, Object>> getTripsResults = jdbcTemplate.queryForList(getTrip, userID);
				
				if(!getTripsResults.isEmpty()) {
					for (Map<String, Object> trips : getTripsResults) {
						String tripId = (String) trips.get("TripID");
						String tripDate = (String) trips.get("TripDate");
						String tripDestination = (String) trips.get("Trip_Destination");
						Integer tripDuration = (Integer)trips.get("TripDuration");
						//Add results to Thymeleaf.
						model.addAttribute("trips", getTripsResults);
				}
					
				}else {
				
				}
				
		String currency = exchangeRate.getExchangeRate("USD", "GBP");
		
		model.addAttribute("exchangeRate", currency);
		model.addAttribute("tripID", tripID);
		model.addAttribute("currencyRate", currency);
		return "myInterestedTrips";
	}
	
	
	@PostMapping("/getexchangeRate")
	public String getexchangeRate(@RequestParam(name="sourceCurrency")String sourceCurrency, 
			@RequestParam(name="targetCurrency")String targetCurrency, 
			@CookieValue(value="username")String username,
			@RequestParam(name="tripID")String tripID,
			Model model) throws IOException, InterruptedException {
				
		String currency = exchangeRate.getExchangeRate(sourceCurrency, targetCurrency);
		
		//Get UserID
		String sql = "SELECT UserID from User WHERE Username =?";
		String userID = jdbcTemplate.queryForObject(sql, String.class, username);
	
		
		String sql2 = "SELECT COUNT(*) FROM User_Trip WHERE UserID=?";
		Integer tripCount = jdbcTemplate.queryForObject(sql2, Integer.class, userID);
		
		model.addAttribute("error", "Number of trips you have declared an interest in: " + tripCount);
		//Return trips from User_Trip table where User has declared interest in trip(s)
		
		String getTrip = "SELECT TripID, TripDate, Trip_Destination, TripDuration FROM User_Trip WHERE UserID=?";

		
		List<Map<String, Object>> getTripsResults = jdbcTemplate.queryForList(getTrip, userID);
		
		if(!getTripsResults.isEmpty()) {
			for (Map<String, Object> trips : getTripsResults) {
				String tripId = (String) trips.get("TripID");
				String tripDate = (String) trips.get("TripDate");
				String tripDestination = (String) trips.get("Trip_Destination");
				Integer tripDuration = (Integer)trips.get("TripDuration");
				//Add results to Thymeleaf.
				model.addAttribute("trips", getTripsResults);
		}
			
		}else {
		
		}
		
			model.addAttribute("exchangeRate", currency);
			model.addAttribute("tripID", tripID);
			model.addAttribute("currencyRate", currency);
			
			return "myInterestedTrips";				
	}	
	@PostMapping("/removeTrip")
	public String removeTrip(@CookieValue(value="username")String username,
			@RequestParam(name="tripID")String tripID,
			Model model) {
		
		//Update 'TripInterest' When a use undeclares their interest
		String getCount = "SELECT TripInterest FROM Trip WHERE TripID=?";
		Integer count = jdbcTemplate.queryForObject(getCount, Integer.class, tripID);
		count --;
		
		
		
		//Remove Trip from User_Trip table. Then reload with updated list of interested trips from the user.
		//Select COUNT of items the user has declared interest in.
		
		String user = "SELECT UserID FROM User WHERE Username=?";
		String userID = jdbcTemplate.queryForObject(user, String.class, username);
		
		String removeTrip = "DELETE FROM User_Trip WHERE UserID =? AND TripID =?";
		jdbcTemplate.update(removeTrip, userID, tripID);
		
		String updateCount = "UPDATE Trip SET TripInterest =? WHERE TripID=?";
		jdbcTemplate.update(updateCount,count,tripID);
		
		
		
		String sql = "SELECT COUNT(*) FROM User_Trip WHERE UserID=?";
		Integer tripCount = jdbcTemplate.queryForObject(sql, Integer.class, userID);
		String getTrip = "SELECT TripID, TripDate, Trip_Destination, TripDuration FROM User_Trip WHERE UserID=?";
		List<Map<String, Object>> getTripsResults = jdbcTemplate.queryForList(getTrip, userID);
		
		if(!getTripsResults.isEmpty()) {
			for (Map<String, Object> trips : getTripsResults) {
				String tripId = (String) trips.get("TripID");
				String tripDate = (String) trips.get("TripDate");
				String tripDestination = (String) trips.get("Trip_Destination");
				Integer tripDuration = (Integer)trips.get("TripDuration");
				//Add results to Thymeleaf.
				model.addAttribute("trips", getTripsResults);
		}
			
		}else {
		
		}
				
		model.addAttribute("error", "Number of trips you have declared an interest in: " + tripCount);
		return "myInterestedTrips";
	}
}
