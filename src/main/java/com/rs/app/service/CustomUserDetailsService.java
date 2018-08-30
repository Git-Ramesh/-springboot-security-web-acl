package com.rs.app.service;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.rs.app.repository.UserRepository;

@Component("customUserDetailsService")
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		com.rs.app.model.User user = userRepository.getActiveUser(username);
		System.out.println(user);
		if (user == null) {
            /*return new org.springframework.security.core.userdetails.User(
              " ", " ", true, true, true, true, 
              getAuthorities(Arrays.asList(
                roleRepository.findByName("ROLE_USER"))));*/
			//return new User("unkown", "unkown", true, true, true, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
			throw new UsernameNotFoundException("No user found");
        }
 
		GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
		User userDetails = new User(username, user.getPassword(), Arrays.asList(authority));
		//userDetails = (User) User.withDefaultPasswordEncoder().username(username).password(user.getPassword()).authorities(user.getRole()).build();
		return userDetails;
	}

}
