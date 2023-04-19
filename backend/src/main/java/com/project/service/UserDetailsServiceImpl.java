package com.project.service;

import java.util.ArrayList;
import java.util.Collection;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.project.entity.AppUser;
import com.project.entity.UserRole;

@Service
@Transactional
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
	private final AccountService accountService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		AppUser appUser = accountService.findByEmail(email);
		if (appUser == null) {
			throw new UsernameNotFoundException("Username " + email + " was not found");
		}
		Collection<GrantedAuthority> authorities = new ArrayList<>();
		UserRole userRole = appUser.getUserRole();
		authorities.add(new SimpleGrantedAuthority(userRole.getRole()));
		return new User(appUser.getUsername(), appUser.getPassword(), authorities);
	}

}
