package no.stacc.payforjoy.interfaces.repository;

import no.stacc.payforjoy.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Find all accounts for a specific user
     */
    List<Account> findByUserId(Long userId);

    /**
     * Find account by account number
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Check if account number already exists
     */
    boolean existsByAccountNumber(String accountNumber);

    /**
     * Find accounts by user ID and account type
     */
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.accountType = :accountType")
    List<Account> findByUserIdAndAccountType(@Param("userId") Long userId, @Param("accountType") String accountType);

    /**
     * Calculate total balance for a user
     */
    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);

    /**
     * Find accounts with balance greater than specified amount
     */
    @Query("SELECT a FROM Account a WHERE a.user.id = :userId AND a.balance > :minBalance")
    List<Account> findAccountsWithMinimumBalance(@Param("userId") Long userId, @Param("minBalance") BigDecimal minBalance);

    /**
     * Find accounts by currency
     */
    List<Account> findByUserIdAndCurrency(Long userId, String currency);

    /**
     * Count total accounts for a user
     */
    long countByUserId(Long userId);
}
