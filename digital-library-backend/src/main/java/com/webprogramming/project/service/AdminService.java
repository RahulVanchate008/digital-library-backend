package com.webprogramming.project.service;

import java.util.List;

import com.webprogramming.project.model.User;

public interface AdminService {
	
	public String updateRole(int userId, String role);
	
	public String deleteUser(User user);
	
	public List<User> getAllUsers();
	
	public User approveUser(String email);

}
