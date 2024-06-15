package com.rahul.cricketgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CricketGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(CricketGameApplication.class, args);
	}

}
