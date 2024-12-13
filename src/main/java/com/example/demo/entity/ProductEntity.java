package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Entity
@Table(name = "PRODUCT")
@Getter
@Setter
@NoArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROID")
    private Long proId;
    @Column(name = "PRONAME")
    private String proName;
    @Column(name = "PRODUCER")
    private String producer;
    @Column(name = "YEARMAKING")
    private Integer yearMaking;
    @Column(name = "EXPRIDATE")
    private Date expDate;
    @Column(name = "QUALITY")
    private Integer quality;
    @Column(name = "PRICE")
    private Double price;
}
