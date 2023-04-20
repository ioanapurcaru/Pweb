package com.project.service;

import com.project.dto.BookDto;
import com.project.entity.Author;
import com.project.entity.Book;
import com.project.entity.Category;
import com.project.entity.Publisher;
import com.project.repository.AuthorRepository;
import com.project.repository.BookRepository;
import com.project.repository.CategoryRepository;
import com.project.repository.PublisherRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.entity.Book;
import com.project.exception.ResourceNotFoundException;
import com.project.repository.BookRepository;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;



@AllArgsConstructor
@Service
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;



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
//        Author author = authorRepository.findById(bookDto.getAuthor_id())
//                .orElseThrow(() -> new ResourceNotFoundException("Author with id not found " +  bookDto.getAuthor_id()));
        Author author = authorRepository.findByName(bookDto.getAuthor_name())
                .orElseGet(() -> {
                    Author newAuthor = new Author();
                    newAuthor.setName(bookDto.getAuthor_name());
                    newAuthor.setDescription(bookDto.getAuthor_description());
                    return authorRepository.save(newAuthor);
                });
        Book book = new Book(bookDto.getTitle(), bookDto.getDescription(), bookDto.getIsbn(),bookDto.getSerialName(), author);

        Set<Category> categories = new HashSet<>();
        //Long categoryId = null;
        for (Long categoryId : bookDto.getCategories()) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category with id " + categoryId + " not found"));
            categories.add(category);
        }
        book.setCategories(categories);

        Set<Publisher> publishers = new HashSet<>();
        for (Long publisherId : bookDto.getPublishers()) {
            Publisher publisher = publisherRepository.findById(publisherId)
                    .orElseThrow(() -> new EntityNotFoundException("Publisher with id " + publisherId + " not found"));
            publishers.add(publisher);
        }
        book.setPublishers(publishers);

        bookRepository.save(book);

        return mapToDTO(book);
    }

    public BookDto updateBook(Long id, BookDto bookDto) {
        Author author = authorRepository.findByName(bookDto.getAuthor_name())
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
        bookDto.setAuthor_name(book.getAuthor().getName());
        bookDto.setAuthor_description(book.getAuthor().getDescription());
        //bookDto.setAuthor_id(book.getAuthor().getAuthor_id());
        Set<Long> categoryIds = book.getCategories().stream().map(Category::getId).collect(Collectors.toSet());
        bookDto.setCategories(categoryIds);

        Set<Long> publisherIds = book.getPublishers().stream().map(Publisher::getId).collect(Collectors.toSet());
        bookDto.setPublishers(publisherIds);

        return bookDto;
    }
}
