package com.example.demo.service;

import com.example.demo.dto.request.FunctionAddDto;
import com.example.demo.entity.Function;

public interface FunctionService {
    Function add(FunctionAddDto functionAddDto);
}
