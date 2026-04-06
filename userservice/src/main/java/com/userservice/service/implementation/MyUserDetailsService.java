package com.userservice.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.userservice.entity.User;
import com.userservice.repository.UserRepository;
import com.userservice.security.UserPrincipal;



@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository repo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

	User user= repo.findByUserName(username).orElseThrow(()->new UsernameNotFoundException("username not found in the database"));
	
	if (user==null) {
		System.out.println("User 404");
		throw new UsernameNotFoundException("User 404");
	}
		 return new UserPrincipal(user);
	}

}