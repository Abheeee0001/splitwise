package com.example.demo.service.expense;

import java.util.List;
import java.util.Map;

import com.example.demo.dto.ExpenseDTO;
import com.example.demo.model.Expense;

public interface IExpenseService {
	Expense addExpense(ExpenseDTO dto);
    List<Expense> getAllExpenses();
    Expense updateExpense(Long id, ExpenseDTO dto);
    void deleteExpense(Long id);
    Map<String, Object> calculateSettlements();
}
