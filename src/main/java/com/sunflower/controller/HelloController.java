package com.sunflower.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunflower.service.SuppliersService;

@Controller
public class HelloController {
	
	private final SuppliersService suppliersService;
	
	@Autowired
	public HelloController(SuppliersService suppliersService) {
		this.suppliersService = suppliersService;
	}
	
	@RequestMapping(value="/hello", method = RequestMethod.GET)
	public String hello(Model model) {
		suppliersService.findAllSupplierName();
		model.addAttribute("name", "Dear");
		return "hello";
	}

}
