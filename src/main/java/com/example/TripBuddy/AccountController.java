package com.example.TripBuddy;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/account")
public class AccountController {
	@GetMapping
	public String account() {
		return "index";
	}
		
	@GetMapping("/logout")
	public String logout() {
		return "redirect:/index.html";
	}
}
