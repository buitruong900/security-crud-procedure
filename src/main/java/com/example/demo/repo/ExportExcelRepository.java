package com.example.demo.repo;

import com.example.demo.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportExcelRepository extends JpaRepository<ProductEntity,Long>, JpaSpecificationExecutor<ProductEntity> {
}
