package com.project.controller;

import java.util.HashMap;
import java.util.List;

import com.project.dto.ResetPasswordDto;
import com.project.service.AccountService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.entity.AppUser;

@RestController
@AllArgsConstructor
@RequestMapping(value = "/user")
public class AccountController {

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private final AccountService accountService;

	@GetMapping("/list")
	public ResponseEntity<?> getUsersList() {
		List<AppUser> users = accountService.userList();
		if (users.isEmpty()) {
			return new ResponseEntity<>("No Users Found.", HttpStatus.OK);
		}
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping("/{username}")
	public ResponseEntity<?> getUserInfo(@PathVariable String username) {
		AppUser user = accountService.findByUsername(username);
		if (user == null) {
			return new ResponseEntity<>("No Users Found.", HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@GetMapping("/findByUsername/{username}")
	public ResponseEntity<?> getUsersListByUsername(@PathVariable String username) {
		List<AppUser> users = accountService.getUsersListByUsername(username);
		if (users.isEmpty()) {
			return new ResponseEntity<>("No Users Found.", HttpStatus.OK);
		}
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody AppUser user) {
		String name = user.getName();
		String username = user.getUsername();
		if (accountService.findByUsername(username) != null) {
			return new ResponseEntity<>("usernameExist", HttpStatus.CONFLICT);
		}
		String email = user.getEmail();
		if (accountService.findByEmail(email) != null) {
			return new ResponseEntity<>("emailExist", HttpStatus.CONFLICT);
		}
		String password = user.getPassword();
		try {
			AppUser appUser = accountService.saveUser(name, username, email, password);
			return new ResponseEntity<>(appUser, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("An error occured", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AppUser user) {
		String email = user.getEmail();
		AppUser appUser = accountService.findByEmail(email);
		if (appUser == null) {
			return new ResponseEntity<>("Email does not exist", HttpStatus.CONFLICT);
		}
		String password = user.getPassword();
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		if (!encodedPassword.equals(appUser.getPassword())) {
			return new ResponseEntity<>("Incorrect password", HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(user, HttpStatus.OK);
	}

	@PostMapping("/changePassword/{email}")
	public ResponseEntity<String> changePassword(@RequestBody ResetPasswordDto resetPasswordDto, @PathVariable String email) {
		AppUser appUser = accountService.findByEmail(email);
		if (appUser == null) {
			return new ResponseEntity<>("User not found!", HttpStatus.BAD_REQUEST);
		}
		String currentPassword = appUser.getPassword();
		String newPassword = resetPasswordDto.getPassword();
		String confirmpassword = resetPasswordDto.getConfirmPassword();
		if (!newPassword.equals(confirmpassword)) {
			return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
		}
		String userPassword = appUser.getPassword();
		try {
			if (!newPassword.isEmpty() && !StringUtils.isEmpty(newPassword)) {
				if (bCryptPasswordEncoder.matches(currentPassword, userPassword)) {
					accountService.updateUserPassword(appUser, newPassword);
				}
			} else {
				return new ResponseEntity<>("IncorrectCurrentPassword", HttpStatus.BAD_REQUEST);
			}
			return new ResponseEntity<>("Password changed successfully!", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("Error occured: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/resetPassword/{email}")
	public ResponseEntity<String> resetPassword(@PathVariable("email") String email) {
		AppUser user = accountService.findByEmail(email);
		if (user == null) {
			return new ResponseEntity<>("emailNotFound", HttpStatus.BAD_REQUEST);
		}
		accountService.resetPassword(user);
		return new ResponseEntity<>("EmailSent!", HttpStatus.OK);
	}

	@PostMapping("/delete")
	public ResponseEntity<String> deleteUser(@RequestBody HashMap<String, String> mapper) {
		String username = mapper.get("username");
		AppUser user = accountService.findByUsername(username);
		accountService.deleteUser(user);
		return new ResponseEntity<>("User Deleted Successfully!", HttpStatus.OK);
	}

}
