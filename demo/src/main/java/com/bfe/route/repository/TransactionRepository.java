package com.bfe.route.repository;

import com.bfe.route.enums.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Method to find transactions by account ID ordered by date descending
    List<Transaction> findByAccount_IdOrderByTransactionDateDesc(Long accountId);
    
    // Optional: Add a method with @Query annotation for more complex queries
    /*
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.transactionDate DESC")
    List<Transaction> findTransactionsByAccountId(@Param("accountId") Long accountId);
    */
}
