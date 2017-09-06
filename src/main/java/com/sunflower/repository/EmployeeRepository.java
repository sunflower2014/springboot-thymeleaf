package com.sunflower.repository;

import org.springframework.data.repository.CrudRepository;

import com.sunflower.model.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

}
