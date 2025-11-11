package com.car;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MycarApplication {

	public static void main(String[] args) {
		SpringApplication.run(MycarApplication.class, args);
	}

}
