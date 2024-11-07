package com.example.TripBuddy;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

@Controller
public class LoginController {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	
	
	@GetMapping("/login")
	public String showRegister() {
		return "register";
	}
	
	@PostMapping("/login")
	public String processLogin(@RequestParam(name="username")String name, @RequestParam(name="password")String password,RedirectAttributes redirectAttributes, Model model ) {
		
		model.addAttribute("username", name);
		model.addAttribute("password", password);
			
		User user = new User();
		user.setUsername(name);
		user.setPassword(password);
				
		boolean retreivedPass = checkPassword(password, name);
		
		if(retreivedPass == true) {
			model.addAttribute("name", name);
			
			
			
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
			Cookie cookie = new Cookie("username", name);
			response.addCookie(cookie);


			return "login";
			
		}else {
			 model.addAttribute("error", "Username/Password is incorrect, please try again!");	
			 return "index";
		}					
	}
	public boolean checkPassword(String password, String name) {				
		String sql ="SELECT PasswordHash from User WHERE Username =?";
		
		try {
		String storedPass = jdbcTemplate.queryForObject(sql, String.class, name);
		return BCrypt.checkpw(password, storedPass);
		}catch(EmptyResultDataAccessException e) {			 
			return false;
		}
	}			
}
