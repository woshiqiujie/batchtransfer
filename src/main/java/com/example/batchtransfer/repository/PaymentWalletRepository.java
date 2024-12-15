package com.example.batchtransfer.repository;

import com.example.batchtransfer.model.PaymentWallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PaymentWalletRepository extends JpaRepository<PaymentWallet, String> {

    // 根据转出方类型查询钱包信息，并支持分页
    @Query("SELECT p FROM PaymentWallet p " +
            "WHERE (:transferType IS NULL OR p.transferType LIKE %:transferType%) " +
            "AND (:environment IS NULL OR p.environment LIKE %:environment%) " +
            "AND (:walletIdAccount IS NULL OR p.walletIdAccount LIKE %:walletIdAccount%)")
    Page<PaymentWallet> findByCriteria(@Param("transferType") String transferType,
                                       @Param("environment") String environment,
                                       @Param("walletIdAccount") String walletIdAccount,
                                       Pageable pageable);

}
