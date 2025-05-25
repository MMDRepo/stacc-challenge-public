package no.stacc.payforjoy.enums;

public enum AccountType {
    CHECKING("Checking Account"),
    SAVINGS("Savings Account"),
    CREDIT("Credit Account"),
    INVESTMENT("Investment Account");

    private final String displayName;

    AccountType(String displayName){
        this.displayName=displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
