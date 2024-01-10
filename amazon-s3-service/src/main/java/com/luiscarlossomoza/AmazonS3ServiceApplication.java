package com.luiscarlossomoza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class AmazonS3ServiceApplication {
	public String ACCESS_KEY_ID = System.getenv("ACCESS_KEY_ID");
	public String SECRET_KEY = System.getenv("SECRET_KEY");

	public String PORT = System.getenv("PORT");

	public static void main(String[] args) {
		SpringApplication.run(AmazonS3ServiceApplication.class, args);
	}

}
