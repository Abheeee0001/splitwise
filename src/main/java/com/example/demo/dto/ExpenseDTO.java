package com.example.demo.dto;


import java.math.BigDecimal;
import java.util.List;

import com.example.demo.model.enums.SplitType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExpenseDTO {
	
	@NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "PaidBy is required")
    private String paidBy;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Split type is required")
    private SplitType splitType;

    @Size(min = 1, message = "At least one participant is required")
    private List<@Valid ExpenseParticipantDTO> participants;



	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(String paidBy) {
		this.paidBy = paidBy;
	}

	public SplitType getSplitType() {
		return splitType;
	}

	public void setSplitType(SplitType splitType) {
		this.splitType = splitType;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	
	

	public ExpenseDTO(BigDecimal amount, String description, String paidBy, SplitType splitType, String category,
			String currency, List<ExpenseParticipantDTO> participants) {
		super();
		this.amount = amount;
		this.description = description;
		this.paidBy = paidBy;
		this.splitType = splitType;
		this.category = category;
		this.currency = currency;
		this.participants = participants;
	}

	public List<ExpenseParticipantDTO> getParticipants() {
		return participants;
	}

	public void setParticipants(List<ExpenseParticipantDTO> participants) {
		this.participants = participants;
	}

	public ExpenseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

    // Getters and Setters
    
    
}
