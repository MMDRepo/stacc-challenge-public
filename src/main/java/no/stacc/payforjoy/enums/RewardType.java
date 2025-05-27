package no.stacc.payforjoy.enums;

public enum RewardType {
    SAVINGS_MILESTONE("Savings Milestone"),
    GOAL_COMPLETION("Goal Completion"),
    STREAK_BONUS("Streak Bonus"),
    MONTHLY_SAVER("Monthly Saver"),
    FIRST_GOAL("First Goal"),
    BIG_SAVER("Big Saver"),
    TRANSACTION_MILESTONE("Transaction Milestone");

    private final String displayName;

    RewardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

