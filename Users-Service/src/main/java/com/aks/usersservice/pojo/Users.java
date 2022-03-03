package com.aks.usersservice.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Users {
	
	@Id
	@GeneratedValue
	private Long id;
	@Size(min = 2, message = "Name length should be greater than 2.")
	@Pattern(regexp = "^[A-Za-z]+[A-Za-z ]*$", message = "Only alphabets and space allowed")
	private String name;
	@Size(min = 8, message = "Password length should be greater than 8.")
	private String password;

	public Users() {
		super();
	}

	public Users(Long id, String name, String password) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
	}

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
	
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	
	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", name=" + name + "]";
	}
	
	

}
