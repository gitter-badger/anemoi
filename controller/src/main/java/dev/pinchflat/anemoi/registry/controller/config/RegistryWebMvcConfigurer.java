package dev.pinchflat.anemoi.registry.controller.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RegistryWebMvcConfigurer implements WebMvcConfigurer {

	private final List<HandlerMethodArgumentResolver> anemoiArgumentResolvers;

	@Autowired
	protected RegistryWebMvcConfigurer(@Qualifier("anemoi") List<HandlerMethodArgumentResolver> anemoiArgumentResolvers) {
		this.anemoiArgumentResolvers = anemoiArgumentResolvers;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.addAll(0, anemoiArgumentResolvers);
	}

}
