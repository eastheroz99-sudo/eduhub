package com.eduhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EduhubApplication {

	public static void main(String[] args) {
		SpringApplication.run(EduhubApplication.class, args);
	}

}
