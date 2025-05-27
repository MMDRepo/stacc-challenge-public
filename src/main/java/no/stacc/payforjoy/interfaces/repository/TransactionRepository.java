package no.stacc.payforjoy.interfaces.repository;

import no.stacc.payforjoy.model.entity.Transaction;
import no.stacc.payforjoy.model.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserIdOrderByTransactionDateDesc(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactionsByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId")
    List<Transaction> findByAccountUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.account.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT t.account.user FROM Transaction t WHERE t.account.user.id = :userId")
    User findUserById(@Param("userId") Long userId);
}
