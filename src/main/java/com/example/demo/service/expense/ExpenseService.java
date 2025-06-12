package com.example.demo.service.expense;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ExpenseDTO;
import com.example.demo.dto.ExpenseParticipantDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Expense;
import com.example.demo.model.ExpenseParticipant;
import com.example.demo.model.Person;
import com.example.demo.model.enums.SplitType;
import com.example.demo.repository.ExpenseRepository;
import com.example.demo.repository.PersonRepository;

@Service
public class ExpenseService implements IExpenseService {

    @Autowired private ExpenseRepository expenseRepository;
    @Autowired private PersonRepository personRepository;

    @Override
    public Expense addExpense(ExpenseDTO expenseDTO) {
        Expense expense = new Expense();
        expense.setAmount(expenseDTO.getAmount());
        expense.setDescription(expenseDTO.getDescription());
        expense.setSplitType(expenseDTO.getSplitType());
        expense.setCurrency(expenseDTO.getCurrency());
        expense.setCategory(expenseDTO.getCategory());

        Person payer = personRepository.findByName(expenseDTO.getPaidBy())
                          .orElseGet(() -> personRepository.save(new Person(expenseDTO.getPaidBy())));
        expense.setPaidBy(payer);

        Set<ExpenseParticipant> participants = new HashSet<>();

        if (expenseDTO.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("Participants cannot be empty");
        }

        int numParticipants = expenseDTO.getParticipants().size();
        BigDecimal totalAmount = expenseDTO.getAmount();

        if (expenseDTO.getSplitType() == SplitType.EQUAL) {
            BigDecimal equalShare = totalAmount.divide(BigDecimal.valueOf(numParticipants), 2, BigDecimal.ROUND_HALF_UP);

            for (ExpenseParticipantDTO dto : expenseDTO.getParticipants()) {
                Person person = getOrCreatePerson(dto.getName());
                ExpenseParticipant participant = new ExpenseParticipant();
                participant.setExpense(expense);
                participant.setPerson(person);
                participant.setShare(equalShare);
                participants.add(participant);
            }

        } else {
            BigDecimal totalPercentage = BigDecimal.ZERO;

            for (ExpenseParticipantDTO dto : expenseDTO.getParticipants()) {
                Person person = getOrCreatePerson(dto.getName());
                ExpenseParticipant participant = new ExpenseParticipant();
                participant.setExpense(expense);
                participant.setPerson(person);

                if (expenseDTO.getSplitType() == SplitType.PERCENTAGE) {
                    BigDecimal percentage = dto.getShare();
                    BigDecimal shareAmount = totalAmount.multiply(percentage).divide(BigDecimal.valueOf(100));
                    participant.setShare(shareAmount);
                    totalPercentage = totalPercentage.add(percentage);
                } else {
                    participant.setShare(dto.getShare());
                }

                participants.add(participant);
            }

            if (expenseDTO.getSplitType() == SplitType.PERCENTAGE &&
                totalPercentage.compareTo(BigDecimal.valueOf(100)) != 0) {
                throw new IllegalArgumentException("Percentage shares must total 100%");
            }
        }

        expense.setParticipants(participants);
        return expenseRepository.save(expense);
    }


    @Override
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    @Override
    public Expense updateExpense(Long id, ExpenseDTO dto) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));

        expense.setAmount(dto.getAmount());
        expense.setDescription(dto.getDescription());
        expense.setSplitType(dto.getSplitType());
        expense.setCategory(dto.getCategory());

        expense.getParticipants().clear();
        for (ExpenseParticipantDTO part : dto.getParticipants()) {
            Person person = getOrCreatePerson(part.getName());
            ExpenseParticipant ep = new ExpenseParticipant();
            ep.setExpense(expense);
            ep.setPerson(person);
            ep.setShare(part.getShare());
            expense.getParticipants().add(ep);
        }

        return expenseRepository.save(expense);
    }

    
    @Override
    public Set<String> getPeopleInExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));

        Set<String> people = new HashSet<>();
        people.add(expense.getPaidBy().getName());

        for (ExpenseParticipant participant : expense.getParticipants()) {
            people.add(participant.getPerson().getName());
        }

        return people;
    }



    @Override
    public void deleteExpense(Long id) {
        Expense expense = expenseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expense not found with ID: " + id));
        expenseRepository.delete(expense);
    }


    @Override
    public Map<String, Object> calculateSettlements() {
        Map<String, BigDecimal> totalPaid = new HashMap<>();
        Map<String, BigDecimal> totalShare = new HashMap<>();

        List<Expense> expenses = expenseRepository.findAll();
        for (Expense exp : expenses) {
            String payer = exp.getPaidBy().getName();
            totalPaid.put(payer, totalPaid.getOrDefault(payer, BigDecimal.ZERO).add(exp.getAmount()));

            for (ExpenseParticipant ep : exp.getParticipants()) {
                String name = ep.getPerson().getName();
                totalShare.put(name, totalShare.getOrDefault(name, BigDecimal.ZERO).add(ep.getShare()));
            }
        }

        Map<String, BigDecimal> netBalance = new HashMap<>();
        Set<String> allPeople = new HashSet<>();
        allPeople.addAll(totalPaid.keySet());
        allPeople.addAll(totalShare.keySet());

        for (String person : allPeople) {
            BigDecimal paid = totalPaid.getOrDefault(person, BigDecimal.ZERO);
            BigDecimal share = totalShare.getOrDefault(person, BigDecimal.ZERO);
            netBalance.put(person, paid.subtract(share));
        }

        return Map.of(
            "paid", totalPaid,
            "share", totalShare,
            "settlements", netBalance
        );
    }

    private Person getOrCreatePerson(String name) {
        return personRepository.findByName(name).orElseGet(() -> {
            Person person = new Person();
            person.setName(name);
            return personRepository.save(person);
        });
    }
}
