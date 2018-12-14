package com.sonalake.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NetPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetPayApplication.class, args);
	}
}
