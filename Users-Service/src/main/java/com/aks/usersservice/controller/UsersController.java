package com.aks.usersservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.aks.usersservice.pojo.Users;
import com.aks.usersservice.repository.UsersRepository;

@RestController
public class UsersController {

	@Autowired
	private UsersRepository usersRepository;

	@GetMapping("/users")
	public CollectionModel<Users> getUsers() {
		Iterable<Users> allUsers = usersRepository.findAll();
		CollectionModel<Users> model = CollectionModel.of(allUsers);
		model.add(linkTo(methodOn(UsersController.class).getUsers(101L)).withRel("one-user"));
		model.add(linkTo(methodOn(UsersController.class).deleteUser(102L)).withRel("delete-user"));
		model.add(linkTo(methodOn(UsersController.class).getUsers()).withSelfRel());
		return model;
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<?> getUsers(@PathVariable Long id) {
		Optional<Users> user = null;
		if(id!=null)
			user = usersRepository.findById(id);
		if(user.isPresent()) {
			EntityModel<Users> model = EntityModel.of(user.get());
			model.add(linkTo(methodOn(UsersController.class).deleteUser(user.get().getId()))
					.withRel("delete-user"));
			return new ResponseEntity<>(model, HttpStatus.OK);
		}
		throw new IllegalArgumentException("User not Found for specified id.");
	}

	@PostMapping("/users")
	public ResponseEntity<?> createUser(@Valid @RequestBody Users user) {
		Users saved = usersRepository.save(user);
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}

	@DeleteMapping("/users/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		if(id!=null && usersRepository.findById(id).isPresent()) {
			usersRepository.deleteById(id);
			return new ResponseEntity<>("User deleted", HttpStatus.ACCEPTED);
		}
		throw new IllegalArgumentException("Wrong Argument");

	}

}
