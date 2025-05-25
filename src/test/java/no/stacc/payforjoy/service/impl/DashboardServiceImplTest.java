/*
package no.stacc.payforjoy.service.impl;

import no.stacc.payforjoy.interfaces.service.*;
import no.stacc.payforjoy.interfaces.repository.DashboardRepository;
import no.stacc.payforjoy.model.dto.DashboardDto;
import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.model.dto.SavingsGoalDto;
import no.stacc.payforjoy.repository.impl.DashboardRepositoryImpl;
import no.stacc.payforjoy.logging.PayForJoyLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DashboardServiceImpl Tests")
class DashboardServiceImplTest {

    @Mock
    private AccountService accountService;

    @Mock
    private TransactionService transactionService;

    @Mock
    private SavingsGoalService savingsGoalService;

    @Mock
    private RewardService rewardService;

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private PayForJoyLogger logger;

    @InjectMocks
    private DashboardServiceImpl dashboardService;

    private Long userId;
    private DashboardRepositoryImpl.FinancialOverview financialOverview;

    @BeforeEach
    void setUp() {
        userId = 1L;

        financialOverview = DashboardRepositoryImpl.FinancialOverview.builder()
                .totalBalance(BigDecimal.valueOf(10000.00))
                .monthlyIncome(BigDecimal.valueOf(5000.00))
                .monthlyExpenses(BigDecimal.valueOf(3000.00))
                .totalSavings(BigDecimal.valueOf(2000.00))
                .netIncome(BigDecimal.valueOf(2000.00))
                .build();
    }

    @Test
    @DisplayName("Should generate dashboard data successfully")
    void shouldGenerateDashboardDataSuccessfully() {
        // Given
        List<TransactionDto> recentTransactions = Arrays.asList();
        List<SavingsGoalDto> activeGoals = Arrays.asList();

        when(dashboardRepository.getFinancialOverview(userId)).thenReturn(financialOverview);
        when(transactionService.findRecentByUserId(userId, 5)).thenReturn(recentTransactions);
        when(savingsGoalService.findActiveGoalsByUserId(userId)).thenReturn(activeGoals);
        when(savingsGoalService.countActiveGoals(userId)).thenReturn(2L);
        when(savingsGoalService.countCompletedGoals(userId)).thenReturn(1L);
        when(rewardService.getTotalPoints(userId)).thenReturn(150);

        // When
        DashboardDto result = dashboardService.getDashboardData(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalBalance()).isEqualTo(BigDecimal.valueOf(10000.00));
        assertThat(result.getMonthlyIncome()).isEqualTo(BigDecimal.valueOf(5000.00));
        assertThat(result.getMonthlyExpenses()).isEqualTo(BigDecimal.valueOf(3000.00));
        assertThat(result.getActiveGoalsCount()).isEqualTo(2);
        assertThat(result.getCompletedGoalsCount()).isEqualTo(1);
        assertThat(result.getTotalRewardPoints()).isEqualTo(150);

        verify(dashboardRepository, times(1)).getFinancialOverview(userId);
        verify(transactionService, times(1)).findRecentByUserId(userId, 5);
        verify(savingsGoalService, times(1)).findActiveGoalsByUserId(userId);
        verify(rewardService, times(1)).getTotalPoints(userId);
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void shouldHandleServiceExceptionsGracefully() {
        // Given
        when(dashboardRepository.getFinancialOverview(userId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        assertThatThrownBy(() -> dashboardService.getDashboardData(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to generate dashboard data");
    }
}

*/
