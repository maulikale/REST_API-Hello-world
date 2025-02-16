package com.in28minutes.rest.webservices.restful_web_services.user;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

import com.in28minutes.rest.webservices.restful_web_services.jpa.PostRepository;
import com.in28minutes.rest.webservices.restful_web_services.jpa.UserRepository;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {

	private UserRepository userRepository;
	
	private PostRepository postRepository;
	
	public UserJpaResource(UserRepository userRepository, PostRepository postRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}
	
	//will retrieve all user
	@GetMapping("/jpa/users")
	public List<User> retrieveAllUsers(){
		return userRepository.findAll();
	}
	
	//will retrieve specific user
	//demo od hateoas
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id){
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("Id: "+id);
		}
		
		EntityModel<User> entityModel = EntityModel.of(user.get());
		WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());
		entityModel.add(link.withRel("all-user"));
		return entityModel;
	}
	
	//will save user
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user){
		User savedUser = userRepository.save(user);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();
		return ResponseEntity.created(location).build();
	}
	//will delete specific user
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id){
		userRepository.deleteById(id);
	}
	
	//will retrieve specific user's all post
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrieveAllPostForUser(@PathVariable int id){
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) 
			throw new UserNotFoundException("Id: "+id);
		
		return user.get().getPosts();
	}
	
	//will get posts of specific user
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Post> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post){
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) 
			throw new UserNotFoundException("user not available with id of: "+id);
		
		post.setUser(user.get());
		
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	//will retrieve specific user's post
	@GetMapping("/jpa/users/{id}/posts/{postId}")
	public Post retrievePostForUser(@PathVariable int id, @PathVariable int postId){
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) { 
			throw new UserNotFoundException("Id: "+id);
		}else {
			List<Post> posts = user.get().getPosts();
			Optional<Post> matchedPost = posts.stream()
					.filter(post -> post.getId() == postId).findFirst();
			return matchedPost.get();
		}
	}
}
