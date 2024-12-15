package com.example.batchtransfer.repository;

import com.example.batchtransfer.model.BatchTransferSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchTransferSummaryRepository extends JpaRepository<BatchTransferSummary, String> {



    // 根据转出方类型查询钱包信息，并支持分页
    @Query("SELECT p FROM BatchTransferSummary p " +
            "WHERE (:environment IS NULL OR p.environment LIKE %:environment%) " +
            "AND (:batchNo IS NULL OR p.batchNo LIKE %:batchNo%) " +
            "AND (:payerWallet IS NULL OR p.payerWallet LIKE %:payerWallet%) " +
            "AND (:fileType IS NULL OR p.fileType LIKE %:fileType%)")
    Page<BatchTransferSummary> findByCriteria(@Param("environment") String environment,
                                       @Param("batchNo") String batchNo,
                                       @Param("payerWallet") String payerWallet,
                                       @Param("fileType") String fileType,
                                       Pageable pageable);

}
