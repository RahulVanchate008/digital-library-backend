package com.webprogramming.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webprogramming.project.model.User;
import com.webprogramming.project.repository.UserRepository;

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public String updateRole(int userId, String role) {
		// TODO Auto-generated method stub

		userRepository.findAll()
	    .stream()
	    .filter(user -> user.getId() == userId)
	    .findFirst()
	    .ifPresent(user -> {
	        user.setRole(role);
	        userRepository.save(user);
	    });
		return "Role Updated";
	}

	@Override
	public String deleteUser(User user) {
		// TODO Auto-generated method stub
		
		userRepository.deleteById(user.getId());
		return "User Deleted";
	}

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return userRepository.findAll();
	}

	@Override
	public User approveUser(String email) {
		// TODO Auto-generated method stub
		User existingUser = userRepository.findByEmail(email);
		System.out.println(email);
		existingUser.setStatus("APPROVED");
		return userRepository.save(existingUser);
	}
}
