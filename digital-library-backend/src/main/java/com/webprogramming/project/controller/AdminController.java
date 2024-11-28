package com.webprogramming.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webprogramming.project.model.User;
import com.webprogramming.project.service.AdminService;

@CrossOrigin
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	
	@PostMapping("/deleteUser")
	public String deleteUser(@RequestBody User user) {
		adminService.deleteUser(user);
		return "User Deleted";
	}	
	
	@GetMapping("/getAllUsers")
	public List<User> getAllUsers() {
		return adminService.getAllUsers();
	}
	
	@PostMapping("/approveUser")
	public User approveUser(@RequestBody String email) {
		return adminService.approveUser(email);
	}
	
}
