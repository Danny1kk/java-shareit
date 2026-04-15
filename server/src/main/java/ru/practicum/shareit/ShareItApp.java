package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // Эта аннотация заменяет @Configuration, @ComponentScan и включает автоконфигурацию
public class ShareItApp {
	public static void main(String[] args) {
		// Запускает встроенный Tomcat и сканирует компоненты автоматически
		SpringApplication.run(ShareItApp.class, args);
	}
}