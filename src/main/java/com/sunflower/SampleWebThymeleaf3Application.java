package com.sunflower;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;

import com.sunflower.model.Message;
import com.sunflower.repository.InMemoryMessageRepository;
import com.sunflower.repository.MessageRepository;

@SpringBootApplication
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
	
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SampleWebThymeleaf3Application.class, args);
	}

}
