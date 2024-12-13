package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "URL_API_ROLE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlApiRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID")
    private Role role;
    @ManyToOne
    @JoinColumn(name = "URL_API_ID")
    private UrlApi urlApi;
}
