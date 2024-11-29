package com.webprogramming.project.service;

import java.io.IOException;
import java.util.List;

import com.webprogramming.project.model.EtdDto;
import com.webprogramming.project.model.User;

public interface UserService {

	public String saveUser(User user);

	public String login(User user);

	public User updateProfile(User user);
	
	public String generateOtp();
	
	public void createOtpMail(String recipientEmail, String otp);
	
	public String verifyOtp(String email, String otp);
	
	public List<User> viewProfile(String email);

	public String indexDocuments();
	
	public String searchIndex(String searchTerm);
	
	public String getMetaData(String etdId);
	
	public String uploadDocument(EtdDto etdDto);
	
	public String chatbot(String message)throws IOException, InterruptedException;

	public String searchUsingParameters(String key, String query, int range);
}
