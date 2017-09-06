package com.sunflower.repository;

import org.springframework.data.repository.CrudRepository;

import com.sunflower.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

}
