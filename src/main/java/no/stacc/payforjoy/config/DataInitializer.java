package no.stacc.payforjoy.config;

import no.stacc.payforjoy.model.entity.*;
import no.stacc.payforjoy.enums.*;
import no.stacc.payforjoy.interfaces.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final RewardRepository rewardRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            log.info("No existing data found. Initializing sample data...");
            initializeSampleData();
        } else {
            log.info("Sample data already exists. Skipping initialization.");
        }
    }

    private void initializeSampleData() {
        // Create sample user
        User sampleUser = createSampleUser();

        // Create sample accounts
        Account checkingAccount = createCheckingAccount(sampleUser);
        Account savingsAccount = createSavingsAccount(sampleUser);

        // Create sample transactions
        createSampleTransactions(checkingAccount, sampleUser);

        // Create sample savings goals
        createSampleGoals(sampleUser);

        // Create sample rewards
        createSampleRewards(sampleUser);

        log.info("Sample data initialization completed successfully!");
    }

    private User createSampleUser() {
        User user = new User();
        user.setFirstName("Demo");
        user.setLastName("User");
        user.setEmail("demo@payforjoy.com");
        user.setPhone("+4712345678");
        user.setDateOfBirth(LocalDate.of(1990, 5, 15));

        User savedUser = userRepository.save(user);
        log.debug("Created sample user: {}", savedUser.getEmail());
        return savedUser;
    }

    private Account createCheckingAccount(User user) {
        Account account = new Account();
        account.setAccountNumber("ACC" + System.currentTimeMillis());
        account.setAccountType(AccountType.CHECKING);
        account.setBalance(BigDecimal.valueOf(25000.00));
        account.setCurrency(Currency.NOK);
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);
        log.debug("Created checking account: {}", savedAccount.getAccountNumber());
        return savedAccount;
    }

    private Account createSavingsAccount(User user) {
        Account account = new Account();
        account.setAccountNumber("SAV" + System.currentTimeMillis());
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(BigDecimal.valueOf(50000.00));
        account.setCurrency(Currency.NOK);
        account.setUser(user);

        Account savedAccount = accountRepository.save(account);
        log.debug("Created savings account: {}", savedAccount.getAccountNumber());
        return savedAccount;
    }

    private void createSampleTransactions(Account account, User user) {
        // Sample income transaction
        Transaction salaryTransaction = new Transaction();
        salaryTransaction.setTransactionDate(LocalDateTime.now().minusDays(1));
        salaryTransaction.setDescription("Monthly Salary");
        salaryTransaction.setAmount(BigDecimal.valueOf(45000.00));
        salaryTransaction.setTransactionType(TransactionType.INCOME);
        salaryTransaction.setCurrency(Currency.NOK);
        salaryTransaction.setAccount(account);
        salaryTransaction.setUser(user);
        salaryTransaction.setCategory("Salary");
        transactionRepository.save(salaryTransaction);

        // Sample expense transactions
        createExpenseTransaction(account, user, "Grocery Shopping", BigDecimal.valueOf(850.50), 2);
        createExpenseTransaction(account, user,"Gas Station", BigDecimal.valueOf(650.00), 3);
        createExpenseTransaction(account, user,"Restaurant", BigDecimal.valueOf(450.00), 4);
        createExpenseTransaction(account, user,"Netflix Subscription", BigDecimal.valueOf(149.00), 5);

        log.debug("Created sample transactions for account: {}", account.getAccountNumber());
    }

    private void createExpenseTransaction(Account account, User user, String description, BigDecimal amount, int daysAgo) {
        Transaction transaction = new Transaction();
        transaction.setTransactionDate(LocalDateTime.now().minusDays(daysAgo));
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.EXPENSE);
        transaction.setCurrency(Currency.NOK);
        transaction.setAccount(account);
        transaction.setUser(user);
        transaction.setCategory("General");
        transactionRepository.save(transaction);
    }

    private void createSampleGoals(User user) {
        // Emergency fund goal
        SavingsGoal emergencyFund = new SavingsGoal();
        emergencyFund.setName("Emergency Fund");
        emergencyFund.setDescription("Save for unexpected expenses");
        emergencyFund.setTargetAmount(BigDecimal.valueOf(100000.00));
        emergencyFund.setCurrentAmount(BigDecimal.valueOf(25000.00));
        emergencyFund.setTargetDate(LocalDate.now().plusMonths(12));
        emergencyFund.setStatus(GoalStatus.ACTIVE);
        emergencyFund.setCurrency(Currency.NOK);
        emergencyFund.setUser(user);
        savingsGoalRepository.save(emergencyFund);

        // Vacation goal
        SavingsGoal vacationGoal = new SavingsGoal();
        vacationGoal.setName("Dream Vacation");
        vacationGoal.setDescription("Trip to Japan");
        vacationGoal.setTargetAmount(BigDecimal.valueOf(35000.00));
        vacationGoal.setCurrentAmount(BigDecimal.valueOf(8500.00));
        vacationGoal.setTargetDate(LocalDate.now().plusMonths(6));
        vacationGoal.setStatus(GoalStatus.ACTIVE);
        vacationGoal.setCurrency(Currency.NOK);
        vacationGoal.setUser(user);
        savingsGoalRepository.save(vacationGoal);

        log.debug("Created sample savings goals for user: {}", user.getEmail());
    }

    private void createSampleRewards(User user) {
        // First goal reward
        Reward firstGoalReward = new Reward();
        firstGoalReward.setTitle("First Goal Created!");
        firstGoalReward.setDescription("Congratulations on creating your first savings goal!");
        firstGoalReward.setPoints(100);
        firstGoalReward.setRewardType(RewardType.FIRST_GOAL);
        firstGoalReward.setUser(user);
        firstGoalReward.setIsClaimed(false);
        rewardRepository.save(firstGoalReward);

        // Savings milestone reward
        Reward milestoneReward = new Reward();
        milestoneReward.setTitle("Savings Milestone");
        milestoneReward.setDescription("You've saved 25,000 NOK!");
        milestoneReward.setPoints(250);
        milestoneReward.setRewardType(RewardType.SAVINGS_MILESTONE);
        milestoneReward.setUser(user);
        milestoneReward.setIsClaimed(true);
        milestoneReward.setClaimedAt(LocalDateTime.now().minusDays(1));
        rewardRepository.save(milestoneReward);

        log.debug("Created sample rewards for user: {}", user.getEmail());
    }
}