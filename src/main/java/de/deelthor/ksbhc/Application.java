package de.deelthor.ksbhc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static final String HC_MAP_NAME = "springtest";

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
