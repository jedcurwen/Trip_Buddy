package com.example.TripBuddy;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

@RequestMapping("/makeChanges")
public class MakeChangesTrip {	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	
	@PostMapping("/show")
	public String show(@RequestParam(name="tripID")String tripID, Model model) {
		String sql = "SELECT TripDestination  FROM Trip WHERE TripID=?";
		String tripDestination = jdbcTemplate.queryForObject(sql, String.class, tripID);
		
		String getDateDuration = "SELECT TripDate, TripDuration FROM Trip WHERE TripID=?";
		List<Map<String, Object>> resultList = jdbcTemplate.queryForList(getDateDuration, tripID);
		
		if(!resultList.isEmpty()) {
			for (Map<String, Object> tripResult : resultList) {
				String tripDate = (String) tripResult.get("TripDate");
				String tripDuration = (String)tripResult.get("TripDuration");
				
				
				model.addAttribute("date", tripDate);
				model.addAttribute("duration", tripDuration);
				
				
				
			}
		}
		
		
		
		
		model.addAttribute("tripID", tripID);
		model.addAttribute("tripDestination", tripDestination);
		
		return "makeChanges";
	}
	
	
	@PostMapping("/submitChanges")
	public String submitChanges(@RequestParam(name="tripID")String tripID, 
			@RequestParam(name="date")String date, 
			@RequestParam(name="duration")Integer duration, 
			Model model) {
		
		
		
		String updateTrip = "UPDATE Trip SET TripDate=?, TripDuration =? WHERE TripID=?";
		jdbcTemplate.update(updateTrip,date, duration, tripID);
				
		
		
		
		return "manageTrips";
	}
	
	
	
	

}
