package com.example.batchtransfer.repository;

import com.example.batchtransfer.model.BatchTransferSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchTransferSummaryRepository extends JpaRepository<BatchTransferSummary, Long> {


    @Query("SELECT b FROM BatchTransferSummary b WHERE " +
            "( :environment = '' OR b.environment = :environment ) AND " +
            "( :batchNo = '' OR b.batchNo LIKE :batchNo ) AND " +
            "( :payerWallet = '' OR b.payerWallet LIKE :payerWallet ) AND " +
            "( :fileType = '' OR b.fileType LIKE :fileType )")
    List<BatchTransferSummary> queryBatchTransferSummary(
            @Param("environment") String environment,
            @Param("batchNo") String batchNo,
            @Param("payerWallet") String payerWallet,
            @Param("fileType") String fileType);


}
