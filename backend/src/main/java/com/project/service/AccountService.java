package com.project.service;

import com.project.entity.AppUser;
import com.project.entity.Role;
import com.project.entity.UserRole;
import com.project.repository.AppUserRepository;
import com.project.repository.RoleRepository;
import com.project.util.EmailConstructor;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class AccountService {
	private final BCryptPasswordEncoder bCryptPasswordEncoder;

	private final AppUserRepository appUserRepo;

	private final RoleRepository roleRepo;

	private final EmailConstructor emailConstructor;

	private final JavaMailSender mailSender;

	@Transactional
	public AppUser saveUser(String name, String username, String email, String password) {
		String encryptedPassword = bCryptPasswordEncoder.encode(password);
		AppUser appUser = new AppUser();
		appUser.setPassword(encryptedPassword);
		appUser.setName(name);
		appUser.setUsername(username);
		appUser.setEmail(email);
		appUser.setCreatedDate(new Date());
		UserRole userRole = new UserRole(appUser, this.findUserRoleByName("USER"));
		appUser.setUserRole(userRole);
		appUserRepo.save(appUser);
		//mailSender.send(emailConstructor.constructNewUserEmail(appUser, password));

		return appUser;
	}

	public void updateUserPassword(AppUser appUser, String newPassword) {
		String encryptedPassword = bCryptPasswordEncoder.encode(newPassword);
		appUser.setPassword(encryptedPassword);
		appUserRepo.save(appUser);
		mailSender.send(emailConstructor.constructResetPasswordEmail(appUser, newPassword));
	}

	public Role saveRole(Role role) {
		return roleRepo.save(role);
	}

	public AppUser findByUsername(String username) {
		return appUserRepo.findByUsername(username);
	}

	public AppUser findByEmail(String userEmail) {
		return appUserRepo.findByEmail(userEmail);
	}

	public List<AppUser> userList() {
		return appUserRepo.findAll();
	}

	public Role findUserRoleByName(String name) {
		return roleRepo.findRoleByName(name);
	}

	public AppUser findUserById(Long id) {
		return appUserRepo.findUserById(id);
	}

	public void deleteUser(AppUser appUser) {
		appUserRepo.delete(appUser);
	}

	public void resetPassword(AppUser user) {
		String password = RandomStringUtils.randomAlphanumeric(10);
		String encryptedPassword = bCryptPasswordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		appUserRepo.save(user);
		mailSender.send(emailConstructor.constructResetPasswordEmail(user, password));
	}

	public List<AppUser> getUsersListByUsername(String username) {
		return appUserRepo.findByUsernameContaining(username);
	}

}
