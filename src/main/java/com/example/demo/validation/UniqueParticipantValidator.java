package com.example.demo.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.demo.dto.ExpenseParticipantDTO;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UniqueParticipantValidator implements ConstraintValidator<UniqueParticipants, List<ExpenseParticipantDTO>> {

    @Override
    public boolean isValid(List<ExpenseParticipantDTO> participants, ConstraintValidatorContext context) {
        Set<String> names = new HashSet<>();
        for (ExpenseParticipantDTO p : participants) {
            if (!names.add(p.getName())) {
                return false; // duplicate found
            }
        }
        return true;
    }
}
