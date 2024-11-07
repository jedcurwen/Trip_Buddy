package com.example.TripBuddy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/manageTrips")
public class ManageTripsController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@GetMapping("/show")
    public String showTrips(@CookieValue(value="username")String username,Model model) {
		
		String sql = "SELECT UserID FROM User WHERE Username =?";
		String userID = jdbcTemplate.queryForObject(sql, String.class, username);

		String getTrips = "SELECT TripID, TripDate, TripDestination, TripWeather, TripDuration, TripInterest FROM Trip WHERE UserID=?";
		List<Map<String, Object>> tripsResultList = jdbcTemplate.queryForList(getTrips, userID);

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
		    model.addAttribute("error", "No trips found for User: " + userID);	
			 
		}
   
        return "manageTrips";
    }
	
	@PostMapping("/tripInterest")
	public String tripInterest(@RequestParam(name="tripID")String tripID, Model model) {
	    
	    String sql = "SELECT UserID from User_Trip WHERE TripID=?";
	    List<Map<String, Object>> users = jdbcTemplate.queryForList(sql, tripID);
	    
	    List<String> userID = new ArrayList<>();
	    List<Map<String, Object>> interestedUsersList = new ArrayList<>();
	    
	    for (Map<String, Object> user : users) {
	        String ID = (String) user.get("UserID");
	        userID.add(ID);
	    }
	    
	    String userInterest = userID.toString();
	    
	    String[] getUsers = userID.toArray(new String[0]);
	    
	    for (String id : getUsers) {
	        String sql2 = "SELECT Username, Gender, Age, City FROM User WHERE UserID=?";
	        
	        List<Map<String, Object>> user = jdbcTemplate.queryForList(sql2, id);
	        
	        if (!user.isEmpty()) {
	            interestedUsersList.addAll(user);
	            model.addAttribute("interestedUsers", interestedUsersList);
	    	    model.addAttribute("userID", id);
	        }
	    }
	    
	    HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
		Cookie cookie = new Cookie("tripID", tripID);
		response.addCookie(cookie);
	    
	    
	    
	    return "manageTrips";
	}
	@PostMapping("/accept")
	public String accept(@CookieValue(value="tripID")String tripID,
			@RequestParam(name="username")String username,
			Model model) {
		//Get UserID
		
		String sql = "SELECT UserID FROM User WHERE Username=?";
		String userID = jdbcTemplate.queryForObject(sql, String.class, username);
		
		
		String updateTrip = "UPDATE User_Trip SET Status ='Accepted' WHERE TripID=? AND UserID=?";
		jdbcTemplate.update(updateTrip,tripID);				
		return "manageTrips";
	}
	
	@PostMapping("/reject")
	public String reject(@CookieValue(value="tripID")String tripID,
			@RequestParam(name="username")String username,
			Model model) {
		
		String sql = "SELECT UserID FROM User WHERE Username=?";
		String userID = jdbcTemplate.queryForObject(sql, String.class, username);
				
		String updateTrip = "UPDATE User_Trip SET Status ='Rejected' WHERE TripID=? AND UserID=?";
		jdbcTemplate.update(updateTrip,tripID,userID);
				
		return "manageTrips";
	}
	
	@PostMapping("/delete")
	public String deleteTrip(@RequestParam(name="tripID")String tripID,@CookieValue(value="username")String username,Model model) {				
		

		
		String sql = "DELETE FROM Trip WHERE TripID=?";
		jdbcTemplate.update(sql,tripID);
		
		String sql2 = "SELECT UserID FROM User WHERE Username =?";
		String userID = jdbcTemplate.queryForObject(sql2, String.class, username);

		String getTrips = "SELECT TripID, TripDate, TripDestination, TripWeather, TripDuration, TripInterest FROM Trip WHERE UserID=?";
		List<Map<String, Object>> tripsResultList = jdbcTemplate.queryForList(getTrips, userID);

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
		    model.addAttribute("error", "No trips found for User: " + userID);	
			 
		}
		
		return "manageTrips";
	}
	
	@PostMapping("/viewComments")
	public String viewComments(
			@RequestParam(value="tripID")String tripID,
			@CookieValue(value="username")String username,
			Model model) {
		String sql = "SELECT CommentedUser, Comment, CommentDate FROM TripComments WHERE TripID =?";
		List<Map<String,Object>> userCommentResults = jdbcTemplate.queryForList(sql, tripID);
		
		
		
		
		if(!userCommentResults.isEmpty()) {
			for(Map<String,Object> userComments: userCommentResults) {
				String commentedUser = (String) userComments.get("CommentedUser");
				String comment = (String) userComments.get("Comment");
				String date = (String) userComments.get("CommentDate");
										
				System.out.println(userCommentResults);
				
				
				model.addAttribute("viewComments",userCommentResults );
			
			}
		}
		else {
			model.addAttribute("error", "No Comments found!");
		
		}
		return "manageTrips";
	}
}