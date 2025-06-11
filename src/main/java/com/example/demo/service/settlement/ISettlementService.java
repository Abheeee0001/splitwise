package com.example.demo.service.settlement;

import java.util.List;
import java.util.Map;

public interface ISettlementService {
    Map<String, Object> calculateSettlements();       // Detailed balances and simplified text
    Map<String, Double> getBalances();                // Just net balances
    List<Map<String, Object>> calculateMinSettlement(); // Actual minimum transaction list
}
