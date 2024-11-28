package com.webprogramming.project.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.webprogramming.project.model.User;
import com.webprogramming.project.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private final UserRepository userRepository;

	private final JavaMailSender javaMailSender;
	
	Map<String, String> otpStorage = new HashMap<>();


	public UserServiceImpl(UserRepository userRepository, JavaMailSender javaMailSender) {
		super();
		this.userRepository = userRepository;
		this.javaMailSender = javaMailSender;
	}

	@Override
	public String saveUser(User user) {
		// TODO Auto-generated method stub
		if (userRepository.findByEmail(user.getEmail()) != null)
			return "The email entered is already in use";
		else {
			user.setPassword(encodePassword(user.getPassword()));
			userRepository.save(user);
			return "User saved successfully";
		}
	}

	@Override
	public String login(User loginUser) {
		User user = userRepository.findAll().stream()
				.filter(existingUser -> loginUser.getEmail().equals(existingUser.getEmail())).findFirst().orElse(null);
		System.out.println(
				user.getEmail() + decodePassword(user.getPassword()) + loginUser.getEmail() + loginUser.getPassword());
		if (user != null && user.getStatus().equals("APPROVED")
				&& loginUser.getPassword().equals(decodePassword(user.getPassword()))) {
//			String token = jwtUtil.generateToken(user.getRole(), user.getId());
			System.out.println(user.getEmail() + user.getPassword());
			sendOtpToMail(loginUser.getEmail());
			return user.getRole();
		} else {
			return "Failed";
		}
	}

	@Override
	public User updateProfile(User user) {
		User findUser = userRepository.findByEmail(user.getEmail());
//		if (user.getAge())
//		findUser.setAge(user.getAge());
//		System.out.println(findUser.getAge() + findUser.getRole());
		if (user.getPassword() != null)
			System.out.println(user.getPassword());
		findUser.setPassword(encodePassword(user.getPassword()));
		if (user.getPhoneNumber() != null)
			findUser.setPhoneNumber(user.getPhoneNumber());
		return userRepository.save(findUser);
	}

	@Override
	public List<User> viewProfile(String email) {
		// TODO Auto-generated method stub
		return userRepository.findAll().stream().filter(user -> user.getEmail().equals(email))
				.collect(Collectors.toList());
	}

	private String encodePassword(String password) {
		return Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));
	}

	private String decodePassword(String password) {
		return new String(Base64.getDecoder().decode(password), StandardCharsets.UTF_8);
	}

	public String generateOtp() {
		// Generate a random 6-digit OTP
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		return String.valueOf(otp);
	}

	public String sendOtpToMail(String email) {
		EmailValidator emailValidator = EmailValidator.getInstance();
		if (emailValidator.isValid(email.trim())) {
			System.out.println("Valid email address: " + email);
			String otp = generateOtp();
			createOtpMail(email, otp);
			return "sent";
		} else {
			System.out.println("Invalid email address: " + email);
			return "failed";
		}
	}

	public void createOtpMail(String recipientEmail, String otp) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(recipientEmail);
		mailMessage.setSubject("Your OTP Code");
		mailMessage.setText("Your OTP code is: " + otp);
		otpStorage.put(recipientEmail, otp);
		javaMailSender.send(mailMessage);
	}

	@Override
	public String verifyOtp(String email, String otp) {
		// TODO Auto-generated method stub
		if (otp == otpStorage.get(email)) {
			otpStorage.remove(email);
			return "OTP verified";
		}
		else 
			return "Wrong OTP";
	}

}
