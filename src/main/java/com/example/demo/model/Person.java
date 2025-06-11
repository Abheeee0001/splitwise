package com.example.demo.model;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "people")
public class Person {	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    
    @OneToMany(mappedBy = "paidBy")
    @JsonBackReference
    private Set<Expense> expensesPaid = new HashSet<>();

    @OneToMany(mappedBy = "person")
    @JsonManagedReference
    private Set<ExpenseParticipant> expenseShares = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Expense> getExpensesPaid() {
		return expensesPaid;
	}

	public void setExpensesPaid(Set<Expense> expensesPaid) {
		this.expensesPaid = expensesPaid;
	}

	public Set<ExpenseParticipant> getExpenseShares() {
		return expenseShares;
	}

	public void setExpenseShares(Set<ExpenseParticipant> expenseShares) {
		this.expenseShares = expenseShares;
	}

	public Person(String name) {
		super();
		this.name = name;
	}

	public Person() {
		super();
	}
    
}