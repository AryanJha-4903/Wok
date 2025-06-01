package com.Wok.Wok;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

// @SpringBootApplication
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WokApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WokApiApplication.class, args);
	}
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
