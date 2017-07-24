package io.pivotal.pal.tracker;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);

		String forceHttps = System.getenv("SECURITY_FORCE_HTTPS");
		if ("true".equals(forceHttps)) {
			http.requiresChannel().anyRequest().requiresSecure();
		}

		http.authorizeRequests().antMatchers("/**").hasRole("USER").and().httpBasic().and().csrf().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);

		auth.inMemoryAuthentication().withUser("user").password("password").roles("USER");
	}
}