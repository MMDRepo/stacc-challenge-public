package no.stacc.payforjoy.constants;

public final class ApplicationConstants {

    public static final String API_VERSION_V1 = "/api/v1";
    public static final String DEFAULT_CURRENCY = "NOK";
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // Savings Goal Constants
    public static final double MIN_GOAL_AMOUNT = 100.0;
    public static final double MAX_GOAL_AMOUNT = 10000000.0;
    public static final int MIN_GOAL_DURATION_DAYS = 7;
    public static final int MAX_GOAL_DURATION_DAYS = 3650; // 10 years

    // Reward Constants
    public static final int POINTS_PER_SAVINGS_NOK = 1;
    public static final int MILESTONE_BONUS_POINTS = 100;

    private ApplicationConstants() {
        throw new IllegalStateException("Utility class");
    }
}

