package com.example.demo.service.impl;

import com.example.demo.dto.request.*;
import com.example.demo.dto.respone.PageResponse;
import com.example.demo.entity.ProductEntity;
import com.example.demo.repo.ExportExcelRepository;
import com.example.demo.repo.ImportExcelRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.service.ProductService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.DateFormatter;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ExportExcelRepository exportExcelRepository;

    @Autowired
    private ImportExcelRepository importExcelRepository;

    @Override
    public List<ProductEntity> getAll() {
        return productRepository.findAll();
    }

    @Override
    public void delete(ProductDeleteDto productDeleteDto) {
        productRepository.detail(productDeleteDto.getProId());
        productRepository.deleteById(productDeleteDto.getProId());
    }

    @Override
    public void add(ProductAddDto productAddDto) {
        List<String> error = new ArrayList<>();
        if (productAddDto.getProName() == null || productAddDto.getProName().trim().isEmpty()){
            error.add("ProName không được để trống");
        }else if(productAddDto.getProName().length() < 4){
            error.add("ProName phải ít nhất có 4 ký tự");
        }
        if(productAddDto.getProducer() == null ||  productAddDto.getProducer().trim().isEmpty()){
            error.add("Producer không được để trống");
        }
        if (productAddDto.getYearMaking() == null) {
            error.add("YearMaking không được để trống");
        } else {
            int yearMaking = productAddDto.getYearMaking();
            if (yearMaking < 1900) {
                error.add("YearMaking phải lớn hơn hoặc bằng 1900");
            }
            int currentYear = LocalDate.now().getYear();
            if (yearMaking > currentYear) {
                error.add("YearMaking phải nhỏ hơn hoặc bằng năm hiện tại");
            }
        }
        if (productAddDto.getPrice() == null ){
            error.add("Price không được để trống");
        }else if(productAddDto.getPrice() < 0 ){
            error.add("Price phải lớn hơn 0");
        }
        if (productAddDto.getQuality() == null){
            error.add("Quality không được để trống");
        }else if(productAddDto.getQuality() < 0 ){
            error.add("Quality phải lớn hơn 0");
        }
        if (productAddDto.getExpDate() == null){
            error.add("ExpDate không được để trống");
        }
        if (!error.isEmpty()){
            throw new RuntimeException(String.join(",",error));
        }
        productRepository.add(productAddDto);
    }


    @Override
    public ProductEntity detail(ProductFindByIdDto productFindByIdDto) {
        ProductEntity productEntity = productRepository.detail(productFindByIdDto.getProId());
        return productEntity;
    }

    @Override
    public void update(ProductUpdateDto productUpdateDto) {
        productRepository.detail(productUpdateDto.getProId());
        productRepository.update(productUpdateDto);
    }


    @Override
    public PageResponse<ProductEntity> findByPagingCriteria(ProductSeachDto productSeachDto, Pageable pageable) {
        Page<ProductEntity> page = productRepository.findByPagingCriteria(productSeachDto,pageable);
        PageResponse<ProductEntity> pageResponse = new PageResponse<>();
        pageResponse.setTotalPages(page.getTotalPages());
        pageResponse.setTotalElements((int) page.getTotalElements());
        pageResponse.setPage(page.getNumber());
        pageResponse.setSize(page.getSize());
        pageResponse.setContent(page.getContent());
        return  pageResponse;
    }

    @Override
    public List<ProductEntity> exportExcelByFindJpa(ProductSeachDto productSeachDto) {
         return exportExcelRepository.findAll(new Specification<ProductEntity>() {
            @Override
            public Predicate toPredicate(Root<ProductEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                if(productSeachDto !=null){
                    if(productSeachDto.getProName() !=null && !productSeachDto.getProName().isEmpty()){
                        predicates.add(criteriaBuilder.like(root.get("proName"),"%" +productSeachDto.getProName() +"%"));
                    }
                    if(productSeachDto.getProducer() !=null && !productSeachDto.getProducer().isEmpty()){
                        predicates.add(criteriaBuilder.like(root.get("producer"),"%" +productSeachDto.getProducer() +"%"));
                    }
                    if(productSeachDto.getYearMaking() !=null){
                        predicates.add(criteriaBuilder.equal(root.get("yearMaking"),productSeachDto.getYearMaking()));
                    }
                    if (productSeachDto.getStartDate() !=null){
                        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("expDate"),productSeachDto.getStartDate()));
                    }
                    if(productSeachDto.getExpDate() !=null){
                        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("expDate"),productSeachDto.getExpDate()));
                    }
                    if(productSeachDto.getQuality() !=null){
                        predicates.add(criteriaBuilder.equal(root.get("quality"),productSeachDto.getQuality()));
                    }
                    if(productSeachDto.getPrice() !=null){
                        predicates.add(criteriaBuilder.equal(root.get("price"),productSeachDto.getPrice()));
                    }
                }
                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
        });
    }

    @Override
    public List<ProductDto> importExcel(MultipartFile file) throws IOException {
        List<ProductDto> productDtos = new ArrayList<>();
        List<String> error = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                try {
                    ProductDto dto = new ProductDto();
                    String proName = dataFormatter.formatCellValue(row.getCell(0));
                    if (proName == null || proName.isEmpty()) {
                        error.add("proName phải là một chuỗi");
                        continue;
                    }
                    dto.setProName(proName.trim());
                    String producer = dataFormatter.formatCellValue(row.getCell(1));
                    if (producer == null || producer.isEmpty()) {
                        error.add("producer phải là một chuỗi");
                        continue;
                    }

                    dto.setProducer(producer.trim());
                    Cell yearMakingCell = row.getCell(2);
                    if (yearMakingCell == null || yearMakingCell.getCellType() != CellType.NUMERIC) {
                        error.add("yearMaking không hợp lệ");
                        continue;
                    }
                    int yearMaking = (int) yearMakingCell.getNumericCellValue();
                    if (yearMaking < 1900 || yearMaking > LocalDate.now().getYear()) {
                        error.add("yearMaking phải lớn hơn 1900 và nhỏ hơn hoặc bằng năm hiện tại");
                        continue;
                    }
                    dto.setYearMaking(yearMaking);
                    Cell qualityCell = row.getCell(3);
                    if (qualityCell == null || qualityCell.getCellType() != CellType.NUMERIC) {
                        error.add("quality phải là số");
                        continue;
                    }
                    int quality = (int) qualityCell.getNumericCellValue();
                    if (quality < 0) {
                        error.add("quality phải lớn hơn 0");
                        continue;
                    }
                    dto.setQuality(quality);
                    Cell priceCell = row.getCell(4);
                    if (priceCell == null || priceCell.getCellType() != CellType.NUMERIC) {
                        error.add("price phải là số");
                        continue;
                    }
                    int price = (int) priceCell.getNumericCellValue();
                    if (price < 0 ){
                        error.add("price phải lớn hơn 0");
                    }
                    dto.setPrice(priceCell.getNumericCellValue());

                    Cell expDateCell = row.getCell(5);
                    if (expDateCell != null) {
                        if (expDateCell.getCellType() == CellType.NUMERIC) {
                            java.util.Date utilDate = DateUtil.getJavaDate(expDateCell.getNumericCellValue());
                            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                            dto.setExpDate(sqlDate);
                        } else if (expDateCell.getCellType() == CellType.STRING) {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                java.util.Date utilDate = sdf.parse(expDateCell.getStringCellValue());
                                java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                                dto.setExpDate(sqlDate);
                            } catch (ParseException e) {
                                error.add("expDate không hợp lệ : "+ e.getMessage());
                                continue;
                            }
                        } else {
                            error.add("expDate không hợp lệ : sai định dạng ");
                            continue;
                        }
                    } else {
                        error.add("expDate không được để trống");
                        continue;
                    }
                    List<ProductEntity> productEntityList = importExcelRepository.findByProNameAAndProducer(proName,producer);
                    if(!productEntityList.isEmpty()){
                        error.add("proName \"" + proName + "\" với producer \"" + producer + "\" đã tồn tại !");
                    }else {
                        productDtos.add(dto);
                    }

                } catch (Exception e) {
                    error.add("Unexpected error - " + e.getMessage());
                }
            }
        }
        workbook.close();
        if (!error.isEmpty()) {
            throw new RuntimeException("Import bị lỗi : " + String.join(", ", error));
        }
        List<ProductEntity> productEntities = new ArrayList<>();
        for (ProductDto dto : productDtos) {
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProName(dto.getProName());
            productEntity.setProducer(dto.getProducer());
            productEntity.setYearMaking(dto.getYearMaking());
            productEntity.setQuality(dto.getQuality());
            productEntity.setPrice(dto.getPrice());
            productEntity.setExpDate(dto.getExpDate());

            productEntities.add(productEntity);
        }
        exportExcelRepository.saveAll(productEntities);
        return productDtos;
    }

    @Override
    public List<ProductDto> importExcelCheckDuplicate(MultipartFile file) throws IOException {
        List<ProductDto> productDtos = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        List<String> error = new ArrayList<>();
        DataFormatter dataFormatter = new DataFormatter();
        for(int rowIndex = 1 ;rowIndex <= sheet.getLastRowNum() ; rowIndex++){
            Row row = sheet.getRow(rowIndex);
            if(row !=null){
                ProductDto productDto = new ProductDto();
                String proName = dataFormatter.formatCellValue(row.getCell(0));
                if (proName == null || proName.isEmpty()) {
                    error.add("proName phải là một chuỗi ");
                    continue;
                }
                productDto.setProName(proName.trim());
                String producer = dataFormatter.formatCellValue(row.getCell(1));
                if (producer == null || producer.isEmpty()) {
                    error.add("producer phải là một chuỗi");
                    continue;
                }
                productDto.setProducer(producer.trim());
                Cell yearMakingCell = row.getCell(2);
                if (yearMakingCell == null || yearMakingCell.getCellType() != CellType.NUMERIC) {
                    error.add("yearMaking không hợp lệ");
                    continue;
                }
                int yearMaking = (int) yearMakingCell.getNumericCellValue();
                if (yearMaking < 1900 || yearMaking > LocalDate.now().getYear()) {
                    error.add("yearMaking phải lớn hơn 1900 và nhỏ hơn hoặc bằng năm hiện tại");
                    continue;
                }
                productDto.setYearMaking(yearMaking);
                Cell qualityCell = row.getCell(3);
                if (qualityCell == null || qualityCell.getCellType() != CellType.NUMERIC) {
                    error.add("quality phải là số");
                    continue;
                }
                int quality = (int) qualityCell.getNumericCellValue();
                if (quality < 0) {
                    error.add("quality phải lớn hơn 0 ");
                    continue;
                }
                productDto.setQuality(quality);
                Cell priceCell = row.getCell(4);
                if (priceCell == null || priceCell.getCellType() != CellType.NUMERIC) {
                    error.add("price phải là số ");
                    continue;
                }
                int price = (int) priceCell.getNumericCellValue();
                if(price < 0 ){
                    error.add("price phải lớn hơn 0");
                    continue;
                }
                productDto.setPrice(priceCell.getNumericCellValue());

                Cell expDateCell = row.getCell(5);
                if (expDateCell != null) {
                    if (expDateCell.getCellType() == CellType.NUMERIC) {
                        java.util.Date utilDate = DateUtil.getJavaDate(expDateCell.getNumericCellValue());
                        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                        productDto.setExpDate(sqlDate);
                    } else if (expDateCell.getCellType() == CellType.STRING) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        try {
                            java.util.Date utilDate = sdf.parse(expDateCell.getStringCellValue());
                            java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
                            productDto.setExpDate(sqlDate);
                        } catch (ParseException e) {
                            error.add("expDate không hợp lệ :" + e.getMessage());
                            continue;
                        }
                    } else {
                        error.add("expDate không hợp lệ : Sai định dạng ");
                        continue;
                    }
                } else {
                    error.add("expDate không được để trống");
                    continue;
                }
                    productDtos.add(productDto);
            }
        }
        workbook.close();
        List<ProductEntity> productEntities = new ArrayList<>();
        for (ProductDto dto : productDtos){
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProName(dto.getProName());
            productEntity.setProducer(dto.getProducer());
            productEntity.setYearMaking(dto.getYearMaking());
            productEntity.setQuality(dto.getQuality());
            productEntity.setPrice(dto.getPrice());
            productEntity.setExpDate(dto.getExpDate());

            productEntities.add(productEntity);
        }
        importExcelRepository.saveAll(productEntities);
        return productDtos;
    }


}
