package com.sunflower.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SuppliersServiceImpl implements SuppliersService {
	
	@Autowired
	@Qualifier("mysqlJdbcTemplate")
	private JdbcTemplate jdbcTemplate;
	
	@Override
	public List<String> findAllSupplierName() {
		List<String> supNameList = jdbcTemplate.queryForList("SELECT SUP_NAME FROM SUPPLIERS", String.class);
		for (String supName: supNameList) {
			System.out.println(supName);
		}
		return supNameList;
	}

}
