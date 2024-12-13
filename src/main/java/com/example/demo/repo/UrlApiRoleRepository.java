package com.example.demo.repo;

import com.example.demo.dto.request.RoleDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.UrlApiRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UrlApiRoleRepository extends JpaRepository<UrlApiRole,Long> {
    @Query("SELECT u.urlApi.nameUrl FROM UrlApiRole u WHERE u.role.name = :roleName")
    List<String> findUrlByRoleName(String roleName);

    @Transactional
    @Modifying
    @Query("DELETE FROM UrlApiRole ur WHERE ur.role.name = :nameRole AND ur.urlApi.nameUrl IN :nameUrl")
    void deleteByRoleNameUrl(String nameRole,List<String> nameUrl);

}
