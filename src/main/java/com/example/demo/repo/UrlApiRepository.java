package com.example.demo.repo;

import com.example.demo.entity.Role;
import com.example.demo.entity.UrlApi;
import com.example.demo.entity.UrlApiRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface UrlApiRepository extends JpaRepository<UrlApi,Long> {
    @Query(value = "SELECT ua.NAME_URL\n" +
            "FROM url_api ua\n" +
            "JOIN url_api_role uar ON ua.id = uar.url_api_id\n" +
            "JOIN tbl_role r ON r.role_id = uar.role_id\n" +
            "WHERE r.NAME IN (:roles)",nativeQuery = true)
    List<String>findNameUrlByRoles(List<String> roles);

    @Query("SELECT u FROM UrlApi u WHERE u.nameUrl IN :nameUrl")
    List<UrlApi>findByNameUrl(List<String> nameUrl);




}