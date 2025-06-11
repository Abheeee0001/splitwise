package com.example.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.ExpenseDTO;
import com.example.demo.model.Expense;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.expense.IExpenseService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private IExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse<Expense>> addExpense(@Valid @RequestBody ExpenseDTO expenseDTO,
                                                           BindingResult result) {
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, errorMessage));
        }

        try {
            Expense expense = expenseService.addExpense(expenseDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, expense, "Expense added successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, null, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ApiResponse<>(false, null, "Internal error occurred"));
        }
    }



    @GetMapping
    public ResponseEntity<ApiResponse<List<Expense>>> getAllExpenses() {
        List<Expense> expenses = expenseService.getAllExpenses();
        ApiResponse<List<Expense>> response = new ApiResponse<>(true, expenses, "Fetched all expenses");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Expense>> updateExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        Expense updated = expenseService.updateExpense(id, expenseDTO);
        ApiResponse<Expense> response = new ApiResponse<>(true, updated, "Expense updated successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        ApiResponse<Void> response = new ApiResponse<>(true, null, "Expense deleted successfully");
        return ResponseEntity.ok(response);
    }

}
