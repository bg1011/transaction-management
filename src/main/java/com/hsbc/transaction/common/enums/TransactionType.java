package com.hsbc.transaction.common.enums;

/**
 * Enum representing the type of a financial transaction.
 * This enum is used to categorize transactions as either income or expense.
 * The values are stored as strings in the database with a maximum length of 10 characters.
 */
public enum TransactionType {
    /**
     * Represents an income transaction.
     * This type is used when money is being added to the account,
     * such as salary deposits, investment returns, or other forms of income.
     */
    INCOME,

    /**
     * Represents an expense transaction.
     * This type is used when money is being spent or withdrawn from the account,
     * such as purchases, bill payments, or other forms of expenses.
     */
    EXPENSE
}
