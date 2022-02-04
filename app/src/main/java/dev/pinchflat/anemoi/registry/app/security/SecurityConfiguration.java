package dev.pinchflat.anemoi.registry.app.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Validated
@Component
@ConfigurationProperties(prefix= "anemoi.security")
public final class SecurityConfiguration {

	private boolean enabled;
	
	@Nullable
	private String user;
	
	@Nullable
	private String pass;

	@Nullable
	private String realm;
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	@Nullable
	public String getRealm() {
		return realm;
	}

	public void setRealm(@Nullable String realm) {
		this.realm = realm;
	}

	@Nullable
	public String getUser() {
		return user;
	}

	public void setUser(@Nullable String user) {
		this.user = user;
	}

	@Nullable
	public String getPass() {
		return pass;
	}

	public void setPass(@Nullable String pass) {
		this.pass = pass;
	}
}
