package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.response.ApiResponse;
import com.example.demo.service.settlement.ISettlementService;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {

    @Autowired
    private ISettlementService settlementService;

    // GET /api/settlement/summary
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSettlementSummary() {
        Map<String, Object> summary = settlementService.calculateSettlements();
        return ResponseEntity.ok(
            new ApiResponse<>(true, summary, "Settlement summary calculated successfully")
        );
    }

    // GET /api/settlement/balances
    @GetMapping("/balances")
    public ResponseEntity<ApiResponse<Map<String, Double>>> getNetBalances() {
        Map<String, Double> balances = settlementService.getBalances();
        return ResponseEntity.ok(
            new ApiResponse<>(true, balances, "Net balances fetched successfully")
        );
    }

    // GET /api/settlement/minimized
    @GetMapping("/minimized")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMinimizedSettlement() {
        List<Map<String, Object>> settlements = settlementService.calculateMinSettlement();
        return ResponseEntity.ok(
            new ApiResponse<>(true, settlements, "Minimized settlements calculated successfully")
        );
    }
}
