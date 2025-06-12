package com.example.demo.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ExpenseParticipantDTO {
	@NotBlank(message = "Participant name is required")
    private String name;

    @NotNull(message = "Share is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Share must be 0 or more")
    private BigDecimal share;

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getShare() {
        return share;
    }

    public void setShare(BigDecimal share) {
        this.share = share;
    }

	public ExpenseParticipantDTO(@NotBlank(message = "Participant name is required") String name,
			@NotNull(message = "Share is required") @DecimalMin(value = "0.0", inclusive = true, message = "Share must be 0 or more") BigDecimal share) {
		super();
		this.name = name;
		this.share = share;
	}
    
    
}
