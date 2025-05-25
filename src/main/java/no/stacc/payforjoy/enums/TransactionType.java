package no.stacc.payforjoy.enums;

public enum TransactionType {
    INCOME("Income"),
    EXPENSE("Expense"),
    TRANSFER("Transfer"),
    SAVINGS("Savings");

    public final String displayName;

    TransactionType(String displayName){
        this.displayName=displayName;
    }

    public String getDisplayName(){
        return displayName;
    }

}
