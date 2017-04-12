package com.sunflower.model;

import java.time.LocalDate;

public class Person {
	public enum Sex {
		MALE, FEMALE
	}
	
	String name;
	LocalDate birthday;
	Sex gender;
	String emailAddress;
	
	public int getAge() {
		return 0;
	}
	
	public static int compareByAge(Person a, Person b) {
		return a.birthday.compareTo(b.birthday);
	}

}
