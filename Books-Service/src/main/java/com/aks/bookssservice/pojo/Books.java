package com.aks.bookssservice.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

@Entity
public class Books {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Size(min = 2, message = "Book Name should be greater than 2.")
	private String bookName;
	private Long userId;
	
	public Books() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Books(Long id, String bookName, Long userId) {
		super();
		this.id = id;
		this.bookName = bookName;
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "Books [id=" + id + ", bookName=" + bookName + ", userId=" + userId + "]";
	}
	
	
}
