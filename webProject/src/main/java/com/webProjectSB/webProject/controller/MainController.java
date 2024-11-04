package com.webProjectSB.webProject.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.webProjectSB.webProject.UserRepository;
import com.webProjectSB.webProject.entities.User;

@Controller
public class MainController {
    @Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// @Autowired
	// private AuthenticationManager authenticationManager;

	@GetMapping("/registration")
	public String showRegistrationForm() {
		return "registration";
	}

    @GetMapping("/")
	public String home() {
		System.out.println("This is the home page");
		return "home";
	}

	@GetMapping("/contact")
	public String contact() {
		System.out.println("This is the contact page");
		return "contact";
	}

    // Display login page
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

	@PostMapping("/registration")
	public String registerUser(@RequestParam String name, @RequestParam String email,
			@RequestParam String password, @RequestParam MultipartFile image, Model model) {

		// Save user details
		User user = new User();
		user.setName(name);
		user.setEmail(email);

		// Encode the password
		String hashedPassword = passwordEncoder.encode(password);
		user.setPassword(hashedPassword);

		// Handle file upload
		if (!image.isEmpty()) {
			try {
				String uploadDir = "C:/uploads/";
				File uploadDirectory = new File(uploadDir);
				if (!uploadDirectory.exists()) {
					uploadDirectory.mkdirs();
				}

				// Save the uploaded file
				File uploadFile = new File(uploadDir + image.getOriginalFilename());
				image.transferTo(uploadFile);

				// Save image path in the database
				user.setImagePath("/uploads/" + image.getOriginalFilename());

			} catch (IOException e) {
				e.printStackTrace();
				return "error"; // Return an error page if something goes wrong
			}
		}

		// Save the user to the database
		userRepository.save(user);

		// Add user info to the model for success page
		model.addAttribute("user", user);

		return "success"; // Return success view
	}

    // Handle login submission
    @PostMapping("/login")
    public String loginUser(@RequestParam String email, @RequestParam String password, Model model) {
        // Find user by email
        User user = userRepository.findByEmail(email);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // If authentication is successful, add user details to model and return success view
            model.addAttribute("user", user);
            return "Authsuccess"; // Redirect to success page with user details
        } else {
            // If authentication fails, display an error message on the login page
            model.addAttribute("error", "Invalid email or password. Please try again.");
            return "login"; // Reload login page with error
        }
    }
}
