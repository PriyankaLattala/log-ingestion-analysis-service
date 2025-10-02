package com.logs.transform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.logs.transform.datasource.model")
@EnableJpaRepositories(basePackages = "com.logs.transform.datasource.repository")
public class LogTransformServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogTransformServiceApplication.class, args);
	}

}
