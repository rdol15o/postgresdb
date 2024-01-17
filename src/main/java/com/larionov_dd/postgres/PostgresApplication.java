package com.larionov_dd.postgres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class PostgresApplication {
	public static void main(String[] args) {
		SpringApplication.run(PostgresApplication.class, args);
	}

}
