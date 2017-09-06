package com.sunflower;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

import com.sunflower.repository.InMemoryMessageRepository;
import com.sunflower.repository.MessageRepository;
import com.sunflower.repository.StorageProperties;
import com.sunflower.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableCaching
public class SampleWebThymeleaf3Application {
	private static final Logger log = LoggerFactory.getLogger(SampleWebThymeleaf3Application.class);
	
	@Bean
	public MessageRepository messageRepository() {
		return new InMemoryMessageRepository();
	}
	
//	@Bean
//	public Converter<String, Message> messageConverter() {
//		return new Converter<String, Message>() {
//			@Override
//			public Message convert(String id) {
//				return messageRepository().findMessage(Long.valueOf(id));
//			}
//		};
//	}
	
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
