package com.example.batchtransfer.repository;

import com.example.batchtransfer.model.ReceiverInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiverInfoRepository extends JpaRepository<ReceiverInfo, String> {
    // 多条件模糊查询
    @Query("SELECT r FROM ReceiverInfo r " +
            "WHERE (:transferType IS NULL OR r.transferType LIKE %:transferType%) " +
            "AND (:environment IS NULL OR r.environment LIKE %:environment%) " +
            "AND (:receiverId IS NULL OR r.receiverId LIKE %:receiverId%)")
    List<ReceiverInfo> findByCriteria(@Param("transferType") String transferType,
                                      @Param("environment") String environment,
                                      @Param("receiverId") String receiverId);
}
