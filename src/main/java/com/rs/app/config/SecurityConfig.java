package com.rs.app.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private DataSource dataSource;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*http.csrf().disable().authorizeRequests().antMatchers("/user/**").hasAnyRole(
		 * "ADMIN", "USER").and().httpBasic()
		 * .realmName("Topic security application Realm");
		 */
		http.csrf().disable().authorizeRequests().antMatchers("/").permitAll()
			.and().authorizeRequests()
			.antMatchers("/user/**")
			.hasAnyRole("ADMIN", "USER", "VISITOR", "AUDITOR", "ADMINISTRATION")
			.and().formLogin().loginPage("/login")
			.failureUrl("/login?error")
			.usernameParameter("username")
			.passwordParameter("password").and()
			.exceptionHandling().accessDeniedPage("/403")
			.and().logout().logoutUrl("/logout")
			.logoutSuccessUrl("/login?logout")
			.deleteCookies("JSESSIONID").invalidateHttpSession(true).and()
			.rememberMe().and().sessionManagement().maximumSessions(5).maxSessionsPreventsLogin(true);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
