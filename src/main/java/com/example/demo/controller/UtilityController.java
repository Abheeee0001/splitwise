package com.example.demo.controller;

import java.math.BigDecimal;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ExpenseDTO;
import com.example.demo.dto.ExpenseParticipantDTO;
import com.example.demo.model.enums.SplitType;
import com.example.demo.repository.ExpenseParticipantRepository;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.PersonRepository;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.expense.IExpenseService;

@RestController
@RequestMapping("/api/utils")
public class UtilityController {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private ExpenseParticipantRepository participantRepository;
    @Autowired private PersonRepository personRepository;
    @Autowired private IExpenseService expenseService;

    @PostMapping("/reset")
    public ResponseEntity<ApiResponse<String>> resetDatabase() {
        participantRepository.deleteAll();
        expenseRepository.deleteAll();
        personRepository.deleteAll();
        return ResponseEntity.ok(new ApiResponse<>(true, "Database reset", "All data has been deleted"));
    }

    @PostMapping("/sample")
    public ResponseEntity<ApiResponse<String>> populateSampleData() {
        ExpenseDTO expense1 = new ExpenseDTO(
                new BigDecimal("300"),
                "Lunch at Cafe",
                "Alice",
                SplitType.EQUAL,
                "Food",
                "INR",
                Arrays.asList(
                        new ExpenseParticipantDTO("Alice", BigDecimal.ZERO),
                        new ExpenseParticipantDTO("Bob", BigDecimal.ZERO),
                        new ExpenseParticipantDTO("Charlie", BigDecimal.ZERO)
                )
        );

        ExpenseDTO expense2 = new ExpenseDTO(
                new BigDecimal("120"),
                "Groceries",
                "Bob",
                SplitType.PERCENTAGE,
                "Household",
                "INR",
                Arrays.asList(
                        new ExpenseParticipantDTO("Alice", new BigDecimal("30")),
                        new ExpenseParticipantDTO("Bob", new BigDecimal("50")),
                        new ExpenseParticipantDTO("Charlie", new BigDecimal("20"))
                )
        );

        try {
            expenseService.addExpense(expense1);
            expenseService.addExpense(expense2);
            return ResponseEntity.ok(new ApiResponse<>(true, "Sample data created", "2 expenses added"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    new ApiResponse<>(false, null, "Failed to add sample data: " + e.getMessage())
            );
        }
    }
}
