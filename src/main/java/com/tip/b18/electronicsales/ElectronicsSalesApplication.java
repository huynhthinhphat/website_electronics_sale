package com.tip.b18.electronicsales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ElectronicsSalesApplication {
	public static void main(String[] args) {
		SpringApplication.run(ElectronicsSalesApplication.class, args);
	}
}
