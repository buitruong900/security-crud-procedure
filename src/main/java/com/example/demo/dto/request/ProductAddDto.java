package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAddDto {
    @NotBlank(message = "Product khong duoc null")
    private String proName;
    @NotBlank(message = "Producer khong duoc null")
    private String producer;
    @NotNull(message = "Year khong duoc null ")
    private Integer yearMaking;
    @NotNull(message = "Expiry date khong duoc null")
    private Date expDate;
    @NotNull(message = "Quality khong duoc null")
    private Integer quality;
    @NotNull(message = "Price khong duoc null")
    private Double price;
}
