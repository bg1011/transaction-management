package com.hsbc.transaction.model.dto.request;

import com.hsbc.transaction.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Data Transfer Object for updating an existing transaction.
 * Contains validation rules for the update data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionDTO {

    /**
     * Description of the transaction (optional update)
     *
     * <p>Constraints:
     * 1. Maximum length of 255 characters when not empty</p>
     */
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    /**
     * Amount of the transaction (optional update)
     *
     * <p>Constraints:
     * 1. Must be greater than 0 when not empty</p>
     */
    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    /**
     * Type of the transaction (optional update)
     */
    @NotNull(message = "Transaction type cannot be null")
    private TransactionType type;
}
