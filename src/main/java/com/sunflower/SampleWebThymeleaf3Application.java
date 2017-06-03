package com.sunflower;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;

import com.sunflower.model.Message;
import com.sunflower.repository.InMemoryMessageRepository;
import com.sunflower.repository.MessageRepository;
import com.sunflower.repository.StorageProperties;
import com.sunflower.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class SampleWebThymeleaf3Application {
	@Bean
	public MessageRepository messageRepository() {
		return new InMemoryMessageRepository();
	}
	
	@Bean
	public Converter<String, Message> messageConverter() {
		return new Converter<String, Message>() {
			@Override
			public Message convert(String id) {
				return messageRepository().findMessage(Long.valueOf(id));
			}
		};
	}
	
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleWebThymeleaf3Application.class, args);
	}

}
