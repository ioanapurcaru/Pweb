package com.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.entity.AppUser;


public interface AppUserRepository extends JpaRepository<AppUser, Long> {

	AppUser findByUsername(String username);

	AppUser findByEmail(String userEmail);
		
	@Query("SELECT appUser FROM AppUser appUser WHERE appUser.id=:x")
	AppUser findUserById(@Param("x") Long id);

	List<AppUser> findByUsernameContaining(String username);
}
