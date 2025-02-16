package com.in28minutes.rest.webservices.restful_web_services.user;

import java.net.URI;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
public class UserResource {

	private UserDaoService service;
	
	public UserResource(UserDaoService service) {
		this.service = service;
	}
	
	//will retrieve all user
	@GetMapping("/users")
	public List<User> retrieveAllUsers(){
		return service.findAll();
	}
	
	//will retrieve specific user
	//demo od hateoas
	@GetMapping("/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id){
		User user = service.findOne(id);
		if(user == null) {
			throw new UserNotFoundException("Id: "+id);
		}
		
		EntityModel<User> entityModel = EntityModel.of(user);
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-user"));
		return entityModel;
	}
	
	//will save user
	@PostMapping("/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user){
		User savedUser = service.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
	//will delete specific user
	@DeleteMapping("/users/{id}")
	public void deleteUser(@PathVariable int id){
		service.deleteById(id);
	}
}
