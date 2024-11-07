package com.example.TripBuddy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.TripBuddy"})
public class TripBuddyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TripBuddyApplication.class, args);
	}

}
