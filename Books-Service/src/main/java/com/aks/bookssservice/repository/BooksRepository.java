package com.aks.bookssservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.aks.bookssservice.pojo.Books;

@Repository
public interface BooksRepository extends CrudRepository<Books, Long> {
	Iterable<Books> findByUserId(Long UserId);
}
