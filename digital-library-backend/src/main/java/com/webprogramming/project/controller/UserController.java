package com.webprogramming.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webprogramming.project.model.User;
import com.webprogramming.project.service.UserService;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public String saveStudent(@RequestBody User user) {
		if (user.getRole() == null)
			user.setRole("STUDENT");
		if (user.getStatus() == null)
			user.setStatus("PENDING");
		return userService.saveUser(user);
	}

	@PostMapping("/login")
	public String login(@RequestBody User user) {
		return userService.login(user);
	}

	@PostMapping("/updateProfile")
	public String updateProfile(@RequestBody User user) {
		userService.updateProfile(user);
		return "Profile Updated";
	}

	@PostMapping("/verifyOtp")
	public String verifyOtp(@RequestBody String email, String otp) {
		return userService.verifyOtp(email, otp);
	}

	@GetMapping("/viewProfile")
	public List<User> viewProfile(@RequestParam("email") String email) {
		return userService.viewProfile(email);
	}

}
