package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findRoleByName(String name);

}
