package com.hsbc.transaction.model.entity;

import com.hsbc.transaction.common.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity class representing a financial transaction.
 * Maps to the 'transactions' table in the database.
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    
    /**
     * Unique identifier for the transaction.
     * Auto-generated using identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Description of the transaction.
     * Cannot be null and has a maximum length of 255 characters.
     */
    @Column(nullable = false, length = 255)
    private String description;

    /**
     * Amount of the transaction.
     * Cannot be null and has precision of 19 digits with 2 decimal places.
     */
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    /**
     * Type of the transaction (INCOME or EXPENSE).
     * Cannot be null and is stored as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    /**
     * Timestamp when the transaction was created.
     * Automatically set by the database.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp when the transaction was last updated.
     * Automatically updated by the database.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}