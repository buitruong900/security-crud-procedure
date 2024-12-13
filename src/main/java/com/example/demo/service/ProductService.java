package com.example.demo.service;

import com.example.demo.dto.request.*;
import com.example.demo.dto.respone.PageResponse;
import com.example.demo.entity.ProductEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductEntity> getAll();
    void delete(ProductDeleteDto productDeleteDto);
    void add(ProductAddDto productAddDto);
    ProductEntity detail(ProductFindByIdDto productFindByIdDto);
    void update(ProductUpdateDto productUpdateDto);
    PageResponse<ProductEntity> findByPagingCriteria(ProductSeachDto productSeachDto, Pageable pageable);
    List<ProductEntity> exportExcelByFindJpa(ProductSeachDto productSeachDto);
    List<ProductDto> importExcel(MultipartFile file) throws IOException;
    List<ProductDto> importExcelCheckDuplicate(MultipartFile file) throws IOException;
}
