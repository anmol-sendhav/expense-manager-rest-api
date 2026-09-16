package com.expense.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.expense.entity.User;

public interface UserRepository extends CrudRepository<User,Integer>{

	Optional<User> findByName(String name);
	Optional<User> findByEmail(String email);

}
