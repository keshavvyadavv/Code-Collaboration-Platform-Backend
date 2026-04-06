package com.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class UserserviceApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext run = SpringApplication.run(UserserviceApplication.class, args);

	}

}
