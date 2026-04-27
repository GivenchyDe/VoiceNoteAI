package org.example.vo_ai_service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.vo_ai_service.mapper")
public class VoAiServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoAiServiceApplication.class, args);
	}

}
