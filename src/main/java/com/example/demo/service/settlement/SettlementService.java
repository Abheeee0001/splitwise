package com.example.demo.service.settlement;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Expense;
import com.example.demo.model.ExpenseParticipant;
import com.example.demo.repository.ExpenseRepository;

@Service
public class SettlementService implements ISettlementService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public Map<String, Object> calculateSettlements() {
        Map<String, BigDecimal> totalPaid = new HashMap<>();
        Map<String, BigDecimal> totalOwed = new HashMap<>();

        for (Expense expense : expenseRepository.findAll()) {
            String payer = expense.getPaidBy().getName();
            totalPaid.put(payer, totalPaid.getOrDefault(payer, BigDecimal.ZERO).add(expense.getAmount()));

            for (ExpenseParticipant participant : expense.getParticipants()) {
                String name = participant.getPerson().getName();
                totalOwed.put(name, totalOwed.getOrDefault(name, BigDecimal.ZERO).add(participant.getShare()));
            }
        }

        // Calculate net balance
        Map<String, BigDecimal> netBalance = new HashMap<>();
        Set<String> allPeople = new HashSet<>(totalPaid.keySet());
        allPeople.addAll(totalOwed.keySet());

        for (String person : allPeople) {
            BigDecimal paid = totalPaid.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal owes = totalOwed.getOrDefault(person, BigDecimal.ZERO);
            netBalance.put(person, paid.subtract(owes));
        }

        // Detailed info
        Map<String, Map<String, Object>> detailedBalances = new HashMap<>();
        for (String person : netBalance.keySet()) {
            Map<String, Object> info = new HashMap<>();
            info.put("paid", totalPaid.getOrDefault(person, BigDecimal.ZERO));
            info.put("owes", totalOwed.getOrDefault(person, BigDecimal.ZERO));
            info.put("net", netBalance.get(person));
            detailedBalances.put(person, info);
        }

        // Simple raw summary (who owes whom)
        List<String> summary = new ArrayList<>();
        for (Expense expense : expenseRepository.findAll()) {
            String payer = expense.getPaidBy().getName();
            for (ExpenseParticipant participant : expense.getParticipants()) {
                String participantName = participant.getPerson().getName();
                if (!participantName.equals(payer)) {
                    BigDecimal amount = participant.getShare().setScale(2, RoundingMode.HALF_UP);
                    summary.add(participantName + " owes " + payer + " ₹" + amount);
                }
            }
        }

        return Map.of(
            "summary", summary,
            "detailedBalances", detailedBalances
        );
    }


    @Override
    public Map<String, Double> getBalances() {
        Map<String, BigDecimal> totalPaid = new HashMap<>();
        Map<String, BigDecimal> totalOwed = new HashMap<>();

        for (Expense expense : expenseRepository.findAll()) {
            String payer = expense.getPaidBy().getName();
            totalPaid.put(payer, totalPaid.getOrDefault(payer, BigDecimal.ZERO).add(expense.getAmount()));

            for (ExpenseParticipant participant : expense.getParticipants()) {
                String name = participant.getPerson().getName();
                totalOwed.put(name, totalOwed.getOrDefault(name, BigDecimal.ZERO).add(participant.getShare()));
            }
        }

        Map<String, Double> netBalances = new HashMap<>();
        for (String person : totalPaid.keySet()) {
            BigDecimal paid = totalPaid.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal owes = totalOwed.getOrDefault(person, BigDecimal.ZERO);
            netBalances.put(person, paid.subtract(owes).setScale(2, RoundingMode.HALF_UP).doubleValue());
        }
        return netBalances;
    }

    @Override
    public List<Map<String, Object>> calculateMinSettlement() {
        Map<String, BigDecimal> totalPaid = new HashMap<>();
        Map<String, BigDecimal> totalShare = new HashMap<>();

        for (Expense exp : expenseRepository.findAll()) {
            String payer = exp.getPaidBy().getName();
            totalPaid.put(payer, totalPaid.getOrDefault(payer, BigDecimal.ZERO).add(exp.getAmount()));

            for (ExpenseParticipant ep : exp.getParticipants()) {
                String name = ep.getPerson().getName();
                totalShare.put(name, totalShare.getOrDefault(name, BigDecimal.ZERO).add(ep.getShare()));
            }
        }

        // Calculate net
        Map<String, BigDecimal> net = new HashMap<>();
        Set<String> people = new HashSet<>(totalPaid.keySet());
        people.addAll(totalShare.keySet());

        for (String person : people) {
            BigDecimal paid = totalPaid.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal share = totalShare.getOrDefault(person, BigDecimal.ZERO);
            net.put(person, paid.subtract(share));
        }

        // PriorityQueues
        PriorityQueue<Map.Entry<String, BigDecimal>> creditors = new PriorityQueue<>(
                (a, b) -> b.getValue().compareTo(a.getValue())  // max heap
        );
        PriorityQueue<Map.Entry<String, BigDecimal>> debtors = new PriorityQueue<>(
                Comparator.comparing(Entry::getValue)  // min heap
        );

        for (Map.Entry<String, BigDecimal> entry : net.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.offer(Map.entry(entry.getKey(), entry.getValue()));
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.offer(Map.entry(entry.getKey(), entry.getValue().negate())); // store positive debt
            }
        }

        List<Map<String, Object>> transactions = new ArrayList<>();
        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            var debtor = debtors.poll();
            var creditor = creditors.poll();

            BigDecimal min = debtor.getValue().min(creditor.getValue());

            Map<String, Object> txn = new HashMap<>();
            txn.put("from", debtor.getKey());
            txn.put("to", creditor.getKey());
            txn.put("amount", min.setScale(2, RoundingMode.HALF_UP));
            transactions.add(txn);

            BigDecimal debtorRem = debtor.getValue().subtract(min);
            BigDecimal creditorRem = creditor.getValue().subtract(min);

            if (debtorRem.compareTo(BigDecimal.ZERO) > 0) {
                debtors.offer(Map.entry(debtor.getKey(), debtorRem));
            }
            if (creditorRem.compareTo(BigDecimal.ZERO) > 0) {
                creditors.offer(Map.entry(creditor.getKey(), creditorRem));
            }
        }

        return transactions;
    }
}
