package com.in28minutes.rest.webservices.restful_web_services.user;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.stereotype.Component;

@Component
public class UserDaoService {
	
	private static List<User> users = new ArrayList<User>();
	private static int usersCount = 0;
	
	static{
		users.add(new User(++usersCount, "Adam", LocalDate.now().minusYears(30)));
		users.add(new User(++usersCount, "Eve", LocalDate.now().minusYears(25)));
		users.add(new User(++usersCount, "Jim", LocalDate.now().minusYears(20)));
		
	}
	
	//will return all user
	public List<User> findAll(){
		return users;
	}
	
	//will return specific user
	public User findOne(int id){
//		for(User user : users) {
//			if(user.getId().equals(id)) {
//				return user;
//			}
//		}
//		return null;
		
		Predicate<? super User> predicate = user -> user.getId().equals(id);
		return users.stream().filter(predicate).findFirst().orElse(null);
	}
	
	//will save user
	public User saveUser(User user) {
		user.setId(++usersCount);
		users.add(user);
		return user;
	}
	//will delete specific user
		public void deleteById(int id){
			Predicate<? super User> predicate = user -> user.getId().equals(id);
			users.removeIf(predicate);
		}
}
