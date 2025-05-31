package com.hsbc.transaction.model.dto.response;

import com.hsbc.transaction.common.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Value Object for transaction data in responses.
 * Used to transfer transaction data to clients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionVO {
    
    /**
     * Unique identifier of the transaction.
     */
    private Long id;

    /**
     * Description of the transaction.
     */
    private String description;

    /**
     * Amount of the transaction.
     */
    private BigDecimal amount;

    /**
     * Type of the transaction (INCOME or EXPENSE).
     */
    private TransactionType type;

    /**
     * Timestamp when the transaction was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the transaction was last updated.
     */
    private LocalDateTime updatedAt;
}
