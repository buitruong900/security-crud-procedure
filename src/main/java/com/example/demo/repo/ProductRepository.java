package com.example.demo.repo;

import com.example.demo.dto.request.ProductAddDto;
import com.example.demo.dto.request.ProductFindByIdDto;
import com.example.demo.dto.request.ProductSeachDto;
import com.example.demo.dto.request.ProductUpdateDto;
import com.example.demo.entity.ProductEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class ProductRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public List<ProductEntity> findAll() {
    StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PRODUCT_PKG.ALLPRODUCT",ProductEntity.class);
        query.registerStoredProcedureParameter("p_out_cursor", void.class, ParameterMode.REF_CURSOR);
        query.execute();
        List<ProductEntity> resultList = query.getResultList();
        return resultList;
    }
    @Transactional
    public ProductEntity detail(Long id){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PRODUCT_PKG.DETAILPRODUCT");
        query.registerStoredProcedureParameter("p_proid",Long.class,ParameterMode.IN);
        query.setParameter("p_proid",id);
        query.registerStoredProcedureParameter("p_out_cursor", void.class, ParameterMode.REF_CURSOR);
        query.execute();
        List<Object[]> results = query.getResultList();
        if (!results.isEmpty()) {
            Object[] result = results.get(0);
            ProductEntity productEntity = new ProductEntity();
            productEntity.setProId(((BigDecimal) result[0]).longValue());
            productEntity.setProName((String) result[1]);
            productEntity.setProducer((String) result[2]);
            if (result[3] != null) {
                productEntity.setYearMaking(((BigDecimal) result[3]).intValue());
            } else {
                productEntity.setYearMaking(0);
            }

            if (result[4] != null) {
                productEntity.setExpDate(new Date(((Timestamp) result[4]).getTime()));
            } else {
                productEntity.setExpDate(null);
            }
            if (result[5] != null) {
                productEntity.setQuality(((BigDecimal) result[5]).intValue());
            } else {
                productEntity.setQuality(0);
            }
            if (result[6] != null) {
                productEntity.setPrice(((BigDecimal) result[6]).doubleValue());
            } else {
                productEntity.setPrice((double) 0);
            }

            return productEntity;
        }
        return null;
    }

    @Transactional
    public void deleteById(Long id){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PRODUCT_PKG.DELETEPRODUCT");
        query.registerStoredProcedureParameter("p_proid",Long.class,ParameterMode.IN);
        query.setParameter("p_proid",id);
        query.execute();
    }

    @Transactional
    public void add(ProductAddDto productAddDto){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PRODUCT_PKG.ADDPRODUCT");
        query.registerStoredProcedureParameter("p_proname",String.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_producer",String.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_yearmaking",Integer.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_expridate", Date.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quality",Integer.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_price",Double.class,ParameterMode.IN);
        query.setParameter("p_proname",productAddDto.getProName());
        query.setParameter("p_producer",productAddDto.getProducer());
        query.setParameter("p_yearmaking",productAddDto.getYearMaking());
        query.setParameter("p_expridate",productAddDto.getExpDate());
        query.setParameter("p_quality",productAddDto.getQuality());
        query.setParameter("p_price",productAddDto.getPrice());
        query.execute();
    }

    @Transactional
    public void update(ProductUpdateDto productUpdateDto){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PRODUCT_PKG.UPDATEPRODUCT");
        query.registerStoredProcedureParameter("p_proid",Long.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_proname",String.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_producer",String.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_yearmaking",Integer.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_expridate", Date.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quality",Integer.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_price",Double.class,ParameterMode.IN);
        query.setParameter("p_proid",productUpdateDto.getProId());
        query.setParameter("p_proname",productUpdateDto.getProName());
        query.setParameter("p_producer",productUpdateDto.getProducer());
        query.setParameter("p_yearmaking",productUpdateDto.getYearMaking());
        query.setParameter("p_expridate",productUpdateDto.getExpDate());
        query.setParameter("p_quality",productUpdateDto.getQuality());
        query.setParameter("p_price",productUpdateDto.getPrice());
        query.execute();
    }

    @Transactional
    public Page<ProductEntity> findByPagingCriteria(ProductSeachDto productSeachDto, Pageable pageable){
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery("PRODUCT_PKG.SEARCHPRODUCT",ProductEntity.class);
        query.registerStoredProcedureParameter("p_proname", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_producer", String.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_yearmaking", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_start_date",Date.class,ParameterMode.IN);
        query.registerStoredProcedureParameter("p_expridate", Date.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_quality", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_price", Double.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pageNum", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_pageSize", Integer.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_total", Integer.class, ParameterMode.OUT);
        query.registerStoredProcedureParameter("p_out_cursor", void.class, ParameterMode.REF_CURSOR);

        query.setParameter("p_proname", productSeachDto.getProName());
        query.setParameter("p_producer", productSeachDto.getProducer());
        query.setParameter("p_yearmaking", productSeachDto.getYearMaking());
        query.setParameter("p_start_date",productSeachDto.getStartDate());
        query.setParameter("p_expridate", productSeachDto.getStartDate());
        query.setParameter("p_expridate", productSeachDto.getExpDate());
        query.setParameter("p_quality", productSeachDto.getQuality());
        query.setParameter("p_price", productSeachDto.getPrice());
        query.setParameter("p_pageNum", pageable.getPageNumber());
        query.setParameter("p_pageSize", pageable.getPageSize());
        query.execute();
        Integer totalRecords = (Integer) query.getOutputParameterValue("p_total");

        List<ProductEntity> products = query.getResultList();

        return new PageImpl<>(products, pageable, totalRecords);
    }
}
