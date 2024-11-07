package com.example.TripBuddy;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/searchTrips")
public class SearchTripController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private LocalDate dateTime = java.time.LocalDate.now();
	
	
	@GetMapping("/search")
	public String search(@CookieValue(value="username")String username,Model model) {	
				
		//Users cannot declare interest in their own trips. 
		String sql = "SELECT UserID FROM User WHERE Username =?";
		String getUser = jdbcTemplate.queryForObject(sql, String.class, username);
		
		//This sql statement will only return trip information when another user has created it.
		String getTrips = "SELECT TripID, TripDate, TripDestination, TripWeather, TripDuration, TripInterest FROM Trip WHERE UserID !=?";
		
		//Display number of avaliable trips.
		String trips = "SELECT COUNT(*) FROM Trip WHERE UserID !=?";
		Integer tripAmount = jdbcTemplate.queryForObject(trips, Integer.class, getUser);
		
		
		model.addAttribute("trip", "Current number of trips avaliable are: " + tripAmount);
		List<Map<String, Object>> tripsResultList = jdbcTemplate.queryForList(getTrips, getUser);
		
		
		//Here all values are retrieved from the SELECT statement, and placed into a list for displaying in our Thymeleaf.
		if (!tripsResultList.isEmpty()) {
		    for (Map<String, Object> tripResult : tripsResultList) {
		        String tripId = (String) tripResult.get("TripID");
		        String tripDate = (String) tripResult.get("TripDate");
		        String tripDestination = (String) tripResult.get("TripDestination");
		        String tripDuration = (String)tripResult.get("TripDuration");
		        Integer tripInterest = (Integer)tripResult.get("TripInterest");
		        
		        model.addAttribute("trips", tripsResultList);
		    }
		} else {
		    	
			 
		}		
		return "searchTrips";
	}
	@PostMapping("/registerInterest")
	public String registerInterest(
			@RequestParam(name="tripID")String tripID, 
			@RequestParam(name="tripDate")String tripDate,
			@RequestParam(name="tripDuration")Integer duration, 
			@CookieValue(value="username")String username,
			Model model) {
			
		String sql = "SELECT TripInterest FROM Trip WHERE TripID=?";
		Integer tripInterest = jdbcTemplate.queryForObject(sql, Integer.class,tripID);
		
		tripInterest++;
		String user = "SELECT UserID FROM User WHERE Username=?";
		String userID = jdbcTemplate.queryForObject(user, String.class,username);

		String updateTrip = "UPDATE Trip SET TripInterest =?,UserIDInterested=?  WHERE TripID=?";
		jdbcTemplate.update(updateTrip,tripInterest,userID,tripID);	
		
		String tripDestination = "SELECT TripDestination FROM Trip WHERE TripID=?";
		String trip =jdbcTemplate.queryForObject(tripDestination, String.class,tripID);
		
		
		String code = "SELECT Country_Code FROM Trip WHERE TripID=?";
		String countryCode = jdbcTemplate.queryForObject(code, String.class,tripID);
		
		//Check to see if User has already registered interest in given trip
		String checkDuplicate = "SELECT COUNT(*) FROM User_Trip WHERE UserID=? AND TripID=?";
		int resultCount = jdbcTemplate.queryForObject(checkDuplicate, Integer.class, userID, tripID);
		
		//User declares interest in a trip
		if(resultCount == 0) {
			jdbcTemplate.update("INSERT INTO User_Trip(UserID, TripID, TripDate, Trip_Destination, Country_Code, TripDuration,Status)VALUES(?,?,?,?,?,?,'Pending')",
					userID, tripID, tripDate, trip,countryCode,duration);
		}
		else {
			model.addAttribute("error", "User: " + userID + " has clready declared interest in Trip ID: "+ tripID);
		}			
		return "searchTrips";
	}
	@PostMapping("/newComment")
	public String newComment(@RequestParam(name="comment")String comment, 
			@RequestParam(name="tripID")String tripID,
			@CookieValue(value="username")String username) {
		
		//Get user
		String sql = "SELECT Username FROM User WHERE Username=?";
		String userID = jdbcTemplate.queryForObject(sql, String.class, username);
		
		
		//Get Datetime
		DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.now();
			
		
		jdbcTemplate.update("INSERT INTO TripComments(TripID, CommentedUser, Comment, CommentDate)VALUES(?,?,?,?)", 
				tripID,userID, comment, dateTimeFormat.format(dateTime));
		return "searchTrips";
	}
	

}
