package com.robintegg.deploy4j.springbootwebapplicationsample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.config.EnableHypermediaSupport;

@SpringBootApplication
public class SpringBootWebApplicationSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebApplicationSampleApplication.class, args);
	}

}
