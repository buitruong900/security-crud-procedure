package com.example.demo.repo;

import com.example.demo.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    @Query(value = "SELECT f.NAME_FUNCTION,f.NAME_URL, ua.NAME_URL\n" +
            "FROM tbl_role r\n" +
            "JOIN url_api_role uar ON r.role_id = uar.role_id\n" +
            "JOIN url_api ua ON ua.id = uar.url_api_id\n" +
            "JOIN tbl_function f ON f.id = ua.function_id\n" +
            "WHERE r.name IN (:roles )", nativeQuery = true)
    List<String> findRoleFunctionAndPermission(List<String> roles);

    Optional<Role> findByName(String name);
}
