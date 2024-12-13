package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductSeachDto extends PageDto {
    private String proName;
    private String producer;
    private Integer yearMaking;
    private Date expDate;
    private Date startDate;
    private Integer quality;
    private Double price;
}
