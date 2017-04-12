package com.sunflower.repository;

import com.sunflower.model.Message;

public interface MessageRepository {
	Iterable<Message> findAll();

	Message save(Message message);

	Message findMessage(Long id);

	void deleteMessage(Long id);
}
