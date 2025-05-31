package com.hsbc.transaction.model.dto.request;

import com.hsbc.transaction.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Data Transfer Object for creating a new transaction.
 * Contains validation rules for the input data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionDTO {
    
    /**
     * Description of the transaction.
     * Cannot be blank and has a maximum length of 255 characters.
     */
    @NotBlank(message = "Description cannot be empty")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    /**
     * Amount of the transaction.
     * Must be a positive number.
     */
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Type of the transaction (INCOME or EXPENSE).
     * Cannot be null.
     */
    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;
}
