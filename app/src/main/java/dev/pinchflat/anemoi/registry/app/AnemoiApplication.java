package dev.pinchflat.anemoi.registry.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication(scanBasePackages = "dev.pinchflat.anemoi.registry")
public class AnemoiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnemoiApplication.class, args);
	}
}
