package com.expense.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(unique=true)
	private String name;
	@Column(unique=true)
	private String email;
	private String password;
	private Integer age;
	private String address;
	private String city;
	private String phone;
	private String picture;
	
	@OneToMany
	private List<Expenses> expense;
	
	@OneToMany
	private List<Income> income;
	
	@OneToMany
	private List<Budget> budget;
}
