package com.example.batchtransfer.repository;

import com.example.batchtransfer.model.PaymentWallet;
import com.example.batchtransfer.model.ReceiverInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentWalletRepository extends JpaRepository<PaymentWallet, String> {
    // 根据转出方类型查询钱包信息
    @Query("SELECT p FROM PaymentWallet p " +
            "WHERE (:transferType IS NULL OR p.transferType LIKE %:transferType%) " +
            "AND (:environment IS NULL OR p.environment LIKE %:environment%) " +
            "AND (:walletIdAccount IS NULL OR p.walletIdAccount LIKE %:walletIdAccount%)")
    List<PaymentWallet> findByCriteria(@Param("transferType") String transferType,
                                       @Param("environment") String environment,
                                       @Param("walletIdAccount") String walletIdAccount);


}

