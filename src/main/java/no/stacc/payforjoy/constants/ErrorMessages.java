package no.stacc.payforjoy.constants;

public final class ErrorMessages {

    public static final String ACCOUNT_NOT_FOUND = "Account not found with ID: %s";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds in account";
    public static final String INVALID_TRANSACTION_AMOUNT = "Transaction amount must be greater than 0";
    public static final String GOAL_NOT_FOUND = "Savings goal not found with ID: %s";
    public static final String INVALID_GOAL_AMOUNT = "Goal amount must be between %s and %s";
    public static final String USER_NOT_FOUND = "User not found with ID: %s";
    public static final String DUPLICATE_ACCOUNT_NUMBER = "Account number already exists";

    private ErrorMessages() {
        throw new IllegalStateException("Utility class");
    }
}
