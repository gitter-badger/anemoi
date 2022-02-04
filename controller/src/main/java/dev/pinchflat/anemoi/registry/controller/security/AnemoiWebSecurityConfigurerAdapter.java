package dev.pinchflat.anemoi.registry.controller.security;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class AnemoiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@NotNull
	protected static final Logger logger = LoggerFactory.getLogger(AnemoiWebSecurityConfigurerAdapter.class);

	@NotNull
	private static final String DEFAULT_REALM = "Realm";

	@NotNull
	private static final String DEFAULT_ROLE = "User";

	@NotNull
	private final SecurityConfiguration securityConfiguration;

	@Autowired
	public AnemoiWebSecurityConfigurerAdapter(@NotNull final SecurityConfiguration securityConfiguration) {
		super();
		this.securityConfiguration = securityConfiguration;
	}

	@Override
	protected void configure(@NotNull final HttpSecurity httpSecurity) throws Exception {
		if (securityConfiguration.isEnabled()) {
			httpSecurity. //
					authorizeRequests().anyRequest().authenticated(). //
					and(). //
					httpBasic().realmName(Optional.ofNullable(securityConfiguration.getRealm()).orElse(DEFAULT_REALM));
			logger.info("Registry is secured with basic authentication!");
		} else {
			logger.warn("Registry is not secure! Not recommended for production environments.");
		}
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		if (securityConfiguration.isEnabled()) {
			logger.info("Basic Authentication User: {}", securityConfiguration.getUser());
			auth.inMemoryAuthentication(). //
					withUser(securityConfiguration.getUser()). //
					password(securityConfiguration.getPass()). //
					roles(DEFAULT_ROLE);
		}
	}

}
