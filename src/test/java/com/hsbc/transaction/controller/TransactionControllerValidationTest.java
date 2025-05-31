package com.hsbc.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.transaction.common.enums.TransactionType;
import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction createValidTransaction() {
        Transaction t = new Transaction();
        t.setDescription("Valid Transaction");
        t.setAmount(new BigDecimal("100.00"));
        t.setType(TransactionType.INCOME);
        return t;
    }

    @Test
    void whenGetWithInvalidPageParam_thenReturn400() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .param("page", "-1") // 无效页码
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGetWithInvalidSortParam_thenReturn400() throws Exception {
        mockMvc.perform(get("/api/transactions")
                        .param("sort", "invalidSort") // 无效排序格式
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenCreateWithInvalidTransaction_thenReturn400() throws Exception {
        CreateTransactionDTO invalid = CreateTransactionDTO.builder()
                .amount(BigDecimal.ZERO)
                .build();

        mockMvc.perform(post("/api/transactions")
                        .header("Idempotency-Key", "test-key") // Add Idempotency-Key header
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.amount").value("Amount must be greater than 0"));
    }

    @Test
    void whenCreateWithBlankDescription_thenReturn400() throws Exception {
        CreateTransactionDTO invalid = CreateTransactionDTO.builder()
                .description("") // Blank description
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.INCOME)
                .build();

        mockMvc.perform(post("/api/transactions")
                .header("Idempotency-Key", "test-key") // Add Idempotency-Key header
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description cannot be empty"));
    }

    @Test
    void whenUpdateWithInvalidId_thenReturn400() throws Exception {
        Transaction valid = createValidTransaction();

        mockMvc.perform(put("/api/transactions/0") // 无效ID
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDeleteWithInvalidId_thenReturn400() throws Exception {
        mockMvc.perform(delete("/api/transactions/0") // 无效ID
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
