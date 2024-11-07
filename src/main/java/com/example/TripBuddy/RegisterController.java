package com.example.TripBuddy;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.mindrot.jbcrypt.BCrypt;//Password encryption

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Controller
public class RegisterController {
	
	private static final String RANDOM_ORG_API_URL = "https://api.random.org/json-rpc/2/invoke";
    private static final String API_KEY = "b2d5abde-1415-4ea7-bfd5-a1ce968e92f1";
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
    private GenerateID generateID; 
	
	@GetMapping("/register")
	public String showRegister() {
		return "register";
	}
	
	@PostMapping("/register")
	public String processUser(@RequestParam(name ="username")String name, 
			@RequestParam(name="password")String password,
			@RequestParam(name="email")String email, 
			@RequestParam(name="passwordrpt")String passwordRepeat,
			@RequestParam(name="age")Integer age,
			@RequestParam(name="gender")String gender,
			@RequestParam(name="city")String city,
			Model model) {
		/*
		 * Request all parameters from our HTML register form. Process the data
		 * Adding user to the database & hashing the password.
		 */
		
		String hashedPass = hashPassword(password);//Pass our "Password" to the hashPass function
		String randomID = generateID.getJson();
		
		jdbcTemplate.update("INSERT INTO User (UserID,Username,Gender,Age,City,PasswordHash)VALUES(?,?,?,?,?,?)",
				randomID,name,gender,age,city,hashedPass);
		
		System.out.println(email + password);
		return "index";
		
	}
	private static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt()); 
		//Return the hashed password
	}
	
	
	}
