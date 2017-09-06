package com.sunflower.repository;

import com.sunflower.model.Book;

public interface BookRepository {
	Book getByIsbn(String isbn);
}
