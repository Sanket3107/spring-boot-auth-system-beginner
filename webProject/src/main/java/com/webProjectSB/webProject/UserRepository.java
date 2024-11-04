package com.webProjectSB.webProject;


import org.springframework.data.jpa.repository.JpaRepository;

import com.webProjectSB.webProject.entities.User;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
