package com.bfe.route.repository;

import com.bfe.route.enums.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.math.BigDecimal;

@Repository
public interface AccountDetailsRepository extends JpaRepository<Account, Long> {
    
    Optional<Account> findByAccountNumber(String accountNumber);
    Optional<Account> findByAccountNumberAndIfscCode(String accountNumber, String ifscCode);
    long deleteByAccountNumberAndIfscCode(String accountNumber, String ifscCode);
    
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber AND a.id != :excludeId")
    Optional<Account> findByAccountNumberAndIdNot(
        @Param("accountNumber") String accountNumber, 
        @Param("excludeId") Long excludeId
    );

    @Query("SELECT COUNT(a) > 0 FROM Account a WHERE a.accountNumber = :accountNumber AND a.id != :excludeId")
    boolean existsByAccountNumberAndIdNot(
        @Param("accountNumber") String accountNumber, 
        @Param("excludeId") Long excludeId
    );

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.nameOnAccount = :name WHERE a.id = :id")
    int updateName(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.phoneLinked = :phone WHERE a.id = :id")
    int updatePhone(@Param("id") Long id, @Param("phone") String phone);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.ifscCode = :code WHERE a.id = :id")
    int updateIfscCode(@Param("id") Long id, @Param("code") String code);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = :balance WHERE a.id = :id")
    int updateBalance(@Param("id") Long id, @Param("balance") BigDecimal balance);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.bankName = :name WHERE a.id = :id")
    int updateBankName(@Param("id") Long id, @Param("name") String name);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.branch = :branch WHERE a.id = :id")
    int updateBranch(@Param("id") Long id, @Param("branch") String branch);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.savingAmount = :amount WHERE a.id = :id")
    int updateSavingAmount(@Param("id") Long id, @Param("amount") BigDecimal amount);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.status = :status WHERE a.id = :id")
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.accountNumber = :accountNumber WHERE a.id = :id")
    int updateAccountNumber(@Param("id") Long id, @Param("accountNumber") String accountNumber);
    
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Account refresh(@Param("id") Long id);
}
