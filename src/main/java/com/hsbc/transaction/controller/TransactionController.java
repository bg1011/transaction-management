package com.hsbc.transaction.controller;


// Spring framework imports
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Validation imports
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

// Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;

// Project imports
import com.hsbc.transaction.common.response.ApiResponse;
import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.dto.response.TransactionVO;
import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.service.TransactionService;

/**
 * REST Controller for managing financial transactions.
 * Provides endpoints for CRUD operations on transactions with pagination, sorting, and validation.
 */
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Management")
@Validated // Enable method parameter validation
public class TransactionController extends BaseController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Retrieves a paginated list of transactions with sorting support.
     *
     * @param page Page number (0-based)
     * @param size Number of items per page
     * @param sort Sort field and direction (format: field,direction)
     * @return Paginated list of transactions
     */
    @GetMapping
    @Operation(summary = "Get paginated list of transactions")
    public ResponseEntity<ApiResponse<Page<TransactionVO>>> getAllTransactions(
            @Parameter(description = "Page number (0-based)", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "0") @Min(0) int page,

            @Parameter(description = "Page size", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "10") @Min(1) int size,

            @Parameter(description = "Sort field and direction (format: field,direction)",
                    example = "id,desc", in = ParameterIn.QUERY)
            @RequestParam(defaultValue = "id,desc")
            @Pattern(regexp = "^[a-zA-Z]+,(asc|desc)$",
                    message = "Invalid sort parameter format. Expected: 'field,asc|desc'")
                    String sort) {

        Pageable pageable = PageRequest.of(page, size, parseSort(sort));
        Page<TransactionVO> result = transactionService.getAllTransactions(pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * Retrieves a specific transaction by its ID.
     *
     * @param id Transaction ID
     * @return Transaction details if found, 404 if not found
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID")
    public ResponseEntity<Transaction> getTransactionById(
            @Parameter(description = "Transaction ID", required = true, in = ParameterIn.PATH)
            @PathVariable @Min(1) Long id) {

        return transactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Creates a new transaction with idempotency support.
     *
     * @param transaction Transaction data
     * @param idempotencyKey Idempotency key for preventing duplicate transactions
     * @return Created transaction details
     */
    @PostMapping
    @Operation(summary = "Create new transaction")
    public ResponseEntity<ApiResponse<TransactionVO>> createTransaction(
            @Parameter(description = "Transaction data", required = true)
            @RequestBody @Valid CreateTransactionDTO transaction,
            @RequestHeader("Idempotency-Key") String idempotencyKey) {

        TransactionVO saved = transactionService.createTransaction(transaction, idempotencyKey);
        return ResponseEntity.ok(ApiResponse.success(saved));
    }

    /**
     * Updates an existing transaction.
     *
     * @param id Transaction ID
     * @param transaction Updated transaction data
     * @return Updated transaction details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update transaction")
    public ResponseEntity<ApiResponse<TransactionVO>> updateTransaction(
            @Parameter(description = "Transaction ID", required = true, in = ParameterIn.PATH)
            @PathVariable @Min(1) Long id,

            @Parameter(description = "Transaction data", required = true)
            @RequestBody @Valid UpdateTransactionDTO transaction) {

        TransactionVO updated = transactionService.updateTransaction(id, transaction);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }

    /**
     * Deletes a transaction.
     *
     * @param id Transaction ID
     * @return 204 No Content if successful
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete transaction")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(description = "Transaction ID", required = true, in = ParameterIn.PATH)
            @PathVariable @Min(1) Long id) {

        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}