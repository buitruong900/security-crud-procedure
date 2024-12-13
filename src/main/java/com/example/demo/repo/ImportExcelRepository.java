package com.example.demo.repo;

import com.example.demo.dto.request.ProductDto;
import com.example.demo.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ImportExcelRepository extends JpaRepository<ProductEntity,Long> {
    @Query(value = "SELECT * FROM product WHERE proname = :proName AND producer = :producer",nativeQuery = true)
    List<ProductEntity> findByProNameAAndProducer(String proName, String producer);
}
