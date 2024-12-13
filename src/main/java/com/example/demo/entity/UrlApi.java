package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "URL_API")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UrlApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME_URL")
    private String nameUrl;
    @OneToMany(mappedBy = "urlApi")
    private Set<UrlApiRole> urlApiRoles = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "FUNCTION_ID")
    private Function function;
}
