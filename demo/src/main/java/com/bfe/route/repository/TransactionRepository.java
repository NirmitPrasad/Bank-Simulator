package com.bfe.route.repository;

import com.bfe.route.enums.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByAccount_IdOrderByTransactionDateDesc(Long accountId);
    List<Transaction> findByAccount_AccountNumberOrderByTransactionDateDesc(String accountNumber);

}
