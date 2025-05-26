package no.stacc.payforjoy.constants;

public final class ApiEndPoints {

    public static final String BASE_API = "/api/v1";
    public static final String ACCOUNTS = BASE_API + "/accounts";
    public static final String TRANSACTIONS = BASE_API + "/transactions";
    public static final String SAVINGS_GOALS = BASE_API + "/goals";
    public static final String REWARDS = BASE_API + "/rewards";
    public static final String DASHBOARD = BASE_API + "/dashboard";
    public static final String USERS = BASE_API + "/users";

    private ApiEndPoints() {
        throw new IllegalStateException("Utility class");
    }
}

