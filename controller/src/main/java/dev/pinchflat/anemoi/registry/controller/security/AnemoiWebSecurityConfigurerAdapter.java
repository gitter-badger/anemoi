package dev.pinchflat.anemoi.registry.controller.security;

import java.util.Optional;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
class AnemoiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

	@NotNull
	private final Logger logger = LoggerFactory.getLogger(AnemoiWebSecurityConfigurerAdapter.class);

	@NotNull
	private static final String DEFAULT_REALM = "Realm";

	@NotNull
	private static final String DEFAULT_ROLE = "User";

	@NotNull
	private final SecurityConfiguration securityConfiguration;

	@NotNull
	private final AccessDeniedHandler accessDeniedHandler;

	@NotNull
	private final AuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	public AnemoiWebSecurityConfigurerAdapter(@NotNull final SecurityConfiguration securityConfiguration,
			@NotNull @Qualifier("anemoi") final AccessDeniedHandler accessDeniedHandler,
			@NotNull @Qualifier("anemoi") final AuthenticationEntryPoint authenticationEntryPoint) {
		super();
		this.securityConfiguration = securityConfiguration;
		this.accessDeniedHandler = accessDeniedHandler;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	protected void configure(@NotNull final HttpSecurity httpSecurity) throws Exception {
		if (securityConfiguration.isEnabled()) {
			httpSecurity. //
					authorizeRequests().anyRequest().authenticated(). //
					and(). //
					httpBasic().realmName(Optional.ofNullable(securityConfiguration.getRealm()).orElse(DEFAULT_REALM)). //
					and(). //
					exceptionHandling().accessDeniedHandler(accessDeniedHandler). //
					and(). //
					exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
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
