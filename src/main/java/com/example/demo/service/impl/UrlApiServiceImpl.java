package com.example.demo.service.impl;

import com.example.demo.dto.request.UrlApiDto;
import com.example.demo.entity.UrlApi;
import com.example.demo.repo.UrlApiRepository;
import com.example.demo.service.UrlApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UrlApiServiceImpl implements UrlApiService {
    @Autowired
    private UrlApiRepository urlApiRepository;

    @Override
    public List<UrlApiDto> getAll() {
        List<UrlApi> urlApis = urlApiRepository.findAll();
        return urlApis.stream()
                .map(urlApi -> new UrlApiDto(urlApi.getNameUrl()))
                .collect(Collectors.toList());
    }
}
