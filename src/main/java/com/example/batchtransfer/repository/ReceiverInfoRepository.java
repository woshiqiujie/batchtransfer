package com.example.batchtransfer.repository;

import com.example.batchtransfer.model.ReceiverInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiverInfoRepository extends JpaRepository<ReceiverInfo, String> {
    Logger logger = LoggerFactory.getLogger(ReceiverInfoRepository.class);

    // 根据条件查询收款信息，并支持分页
    @Query("SELECT r FROM ReceiverInfo r WHERE " +
            "(:transferType is null or r.transferType LIKE %:transferType%) and " +
            "(:environment is null or r.environment LIKE %:environment%) and " +
            "(:receiverId is null or r.receiverId LIKE %:receiverId%)")
    Page<ReceiverInfo> searchReceiverInfo(@Param("transferType") String transferType,
                                           @Param("environment") String environment,
                                           @Param("receiverId") String receiverId,
                                           Pageable pageable);
}
