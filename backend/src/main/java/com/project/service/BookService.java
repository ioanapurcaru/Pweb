package com.project.service;

import com.project.dto.BookDto;
import com.project.entity.Author;
import com.project.entity.Book;
import com.project.repository.AuthorRepository;
import com.project.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.entity.Book;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.BookRepository;

import javax.transaction.Transactional;



@AllArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public List<BookDto> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found witg id: " + id));
        return mapToDTO(book);
    }

    public BookDto addBook(BookDto bookDto) {
        Author author = authorRepository.findById(bookDto.getAuthor_id())
                .orElseThrow(() -> new ResourceNotFoundException("Author with id not found " +  bookDto.getAuthor_id()));
        Book book = new Book(bookDto.getTitle(), bookDto.getDescription(), bookDto.getIsbn(),bookDto.getSerialName(), author);
        bookRepository.save(book);
//        bookDto.setId(book.getId());
//        return bookDto;
        return mapToDTO(book);
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        Author author = authorRepository.findById(bookDto.getAuthor_id())
                .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDto.getAuthor_id()));
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        book.setTitle(bookDto.getTitle());
        book.setAuthor(author);
        bookRepository.save(book);
        bookDto.setId(book.getId());
        return bookDto;
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Book not found with id: "+ id);
        }
        bookRepository.deleteById(id);
    }

    private BookDto mapToDTO(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setDescription(book.getDescription());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setSerialName(book.getSerialName());
        bookDto.setAuthor_id(book.getAuthor().getAuthor_id());
        return bookDto;
    }
}
