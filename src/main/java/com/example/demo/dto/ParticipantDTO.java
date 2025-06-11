package com.example.demo.dto;

import java.math.BigDecimal;

// Used for clean response

public class ParticipantDTO {
    private String name;
    private BigDecimal share;
	public ParticipantDTO(String name, BigDecimal share) {
		super();
		this.name = name;
		this.share = share;
	}
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
	public ParticipantDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

    // Getters and Setters
    
   
}
