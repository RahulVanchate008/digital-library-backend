package com.webprogramming.project.service;

import java.util.List;

import com.webprogramming.project.model.User;

public interface UserService {

	public String saveUser(User user);

	public String login(User user);

	public User updateProfile(User user);
	
	public String generateOtp();
	
	public void createOtpMail(String recipientEmail, String otp);
	
	public String verifyOtp(String email, String otp);
	
	public List<User> viewProfile(String email);

}
