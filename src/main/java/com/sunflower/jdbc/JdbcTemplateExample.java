package com.sunflower.jdbc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sunflower.model.Customer;

@Component
public class JdbcTemplateExample implements CommandLineRunner {
	
	private static final Logger log = LoggerFactory.getLogger(JdbcTemplateExample.class);
	
	@Autowired	
	@Qualifier("mysqlJdbcTemplate")
	JdbcTemplate mysqlJdbcTemplate;

	@Override
	public void run(String... args) throws Exception {
		log.info("Creating tables");
		
		mysqlJdbcTemplate.execute("DROP TABLE IF EXISTS customers");
		mysqlJdbcTemplate.execute("CREATE TABLE customers(" + 
				"`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT, `first_name` VARCHAR(255), `last_name` VARCHAR(255), PRIMARY KEY (`ID`)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
		
		List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long").stream().map(name -> name.split(" ")).collect(Collectors.toList());
		splitUpNames.forEach(name -> log.info(String.format("Inserting customer record for %s %s", name[0], name[1])));
		
		mysqlJdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) values (?,?)", splitUpNames);
		
		log.info("Querying for customer records where first_name = 'josh':");
		mysqlJdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ?", new Object[] { "Josh"},
				(rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))).forEach(customer -> log.info(customer.toString()));
	}

}
