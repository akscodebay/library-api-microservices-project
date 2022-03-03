package com.aks.bookssservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import com.aks.bookssservice.pojo.Books;
import com.aks.bookssservice.proxy.UsersProxy;
import com.aks.bookssservice.repository.BooksRepository;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
public class BooksController {
	
	private Logger logger = LoggerFactory.getLogger(BooksController.class);
	
	@Autowired
	private BooksRepository booksRepository;
	
	@Autowired
	private UsersProxy usersProxy;
	
	@GetMapping("/books")
	public CollectionModel<Books> getBooks() {
		Iterable<Books> allBooks = booksRepository.findAll();
		CollectionModel<Books> model = CollectionModel.of(allBooks);
		model.add(linkTo(methodOn(BooksController.class).getBooks(101L)).withRel("one-book"));
		model.add(linkTo(methodOn(BooksController.class).deleteBooks(101L)).withRel("delete-book"));
		model.add(linkTo(methodOn(BooksController.class).getBooks()).withSelfRel());
		return model;

	}
	
	@GetMapping("/books/{id}")
	public ResponseEntity<?> getBooks(@PathVariable Long id) {
		Optional<Books> book = null;
		if (id!=null) {
			book = booksRepository.findById(id);
		}
		if(book.isPresent()) {
			EntityModel<Books> model = EntityModel.of(book.get());
			model.add(linkTo(methodOn(BooksController.class).deleteBooks(book.get().getId()))
					.withRel("delete-user"));
			return new ResponseEntity<>(model, HttpStatus.FOUND);
		}
		throw new IllegalArgumentException("Book not found for specified id.");
	}
	
	@GetMapping("/books/available")
	public ResponseEntity<?> getAvailableBooks() {
		Iterable<Books> books = booksRepository.findByUserId(null);
		if(!books.iterator().hasNext()) {
			return new ResponseEntity<>("No Books Available", HttpStatus.NOT_FOUND);
		}
		CollectionModel<?> model = CollectionModel.of(books);
		model.add(linkTo(methodOn(BooksController.class).getBooks(101L)).withRel("one-book"));
		model.add(linkTo(methodOn(BooksController.class).deleteBooks(101L)).withRel("delete-book"));
		model.add(linkTo(methodOn(BooksController.class).issueBook(1L, 1L)).withRel("issue-book"));
		return new ResponseEntity<>(model, HttpStatus.FOUND);
	}
	
	@PostMapping("/books")
	public ResponseEntity<Books> addBooks(@Valid @RequestBody Books book) {
		book.setUserId(null);
		Books save = booksRepository.save(book);
		return new ResponseEntity<>(save, HttpStatus.CREATED);
	}
	
	@DeleteMapping("/books/{id}")
	public ResponseEntity<String> deleteBooks(@PathVariable Long id) {
		if(id!=null) {
			if(booksRepository.findById(id).get().getUserId()!=null) {
				return new ResponseEntity<>("Book Alloted to User. Cannot be deleted.", 
						HttpStatus.CONFLICT);
			}
			booksRepository.deleteById(id);
			return new ResponseEntity<>("Book Deleted", HttpStatus.OK);
		}
		throw new IllegalArgumentException("Wrong Argument");
	}
	
	//@Retry(name = "issue-api", fallbackMethod = "issueBookResponse")
	@CircuitBreaker(name = "default", fallbackMethod = "issueBookResponse")
	@RateLimiter(name = "default")
	@PutMapping("/books/{id}/issue/{userId}")
	public ResponseEntity<String> issueBook(@PathVariable Long id, @PathVariable Long userId) {
		logger.info("retry...");
		Optional<Books> book = booksRepository.findById(id);
		if(book.isPresent() && book.get().getUserId() == null) {
			if(usersProxy.getUsers(userId).getStatusCode() != HttpStatus.OK) {
				throw new IllegalArgumentException("User not found for specified id."); 
			}
			book.get().setUserId(userId);
			booksRepository.save(book.get());
			return new ResponseEntity<>("Book Issued", HttpStatus.OK);
		}
		
		throw new IllegalArgumentException("Book not found in library for specified id.");

	}
	
	@PutMapping("/books/{id}/return")
	public ResponseEntity<String> returnBook(@PathVariable Long id) {
		Optional<Books> book = booksRepository.findById(id);
		if(book.isPresent()) {
			book.get().setUserId(null);
			booksRepository.save(book.get());
			return new ResponseEntity<>("Book Returned", HttpStatus.OK);
		}
		
		throw new IllegalArgumentException("Book not found for specified id.");

	}
	
	
	public ResponseEntity<?> issueBookResponse(CallNotPermittedException ex){
		return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
