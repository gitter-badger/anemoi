package dev.pinchflat.anemoi.registry.controller.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = SecurityConfiguration.class)
@TestPropertySource("classpath:secured.properties")
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
class BindingPropertiesToSecurityConfigurationTest {

	@Autowired
	private SecurityConfiguration securityConfiguration;

	@Test
	void bindSecurityConfiguration() {
		assertTrue(securityConfiguration.isEnabled());
		assertEquals("Anemoi Container Registry", securityConfiguration.getRealm());
		assertEquals("test", securityConfiguration.getUser());
		assertEquals("{noop}pass", securityConfiguration.getPass());
	}
}
