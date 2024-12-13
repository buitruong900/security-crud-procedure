package com.example.demo.service.impl;

import com.example.demo.dto.request.FunctionAddDto;
import com.example.demo.entity.Function;
import com.example.demo.repo.FunctionRepository;
import com.example.demo.service.FunctionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FunctionServiceImpl implements FunctionService {
    @Autowired
    private FunctionRepository functionRepository;
    @Override
    public Function add(FunctionAddDto functionAddDto) {
        Function function = new Function();
        function.setNameFunction(function.getNameFunction());
        return functionRepository.save(function);
    }
}
