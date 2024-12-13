package com.example.demo.repo;

import com.example.demo.entity.User;
import com.example.demo.entity.UsersRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersRolesRepository extends JpaRepository<UsersRoles,Long> {
    List<UsersRoles> findByUser(User user);
}
