package com.example.TripBuddy;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
public class Trip {
	

	public String location;
	public String tripDate;
	public Integer duration;
	
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTripDate() {
		return tripDate;
	}
	public void setTripDate(String tripDate2) {
		this.tripDate = tripDate2;
	}
	public Integer getDuration() {
		return duration;
	}
	public void setDuration(Integer duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "Trip [location=" + location + ", tripDate=" + tripDate + ", getLocation()=" + getLocation()
				+ ", getTripDate()=" + getTripDate() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}
	
	
	

}
