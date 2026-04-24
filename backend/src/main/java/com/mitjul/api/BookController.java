package com.mitjul.api;

import com.mitjul.domain.book.BookStatus;
import com.mitjul.dto.book.BookCreateRequest;
import com.mitjul.dto.book.BookDetailResponse;
import com.mitjul.dto.book.BookResponse;
import com.mitjul.dto.book.BookUpdateRequest;
import com.mitjul.service.BookService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponse> getBooks(@RequestParam(required = false) BookStatus status) {
        return bookService.getBooks(status);
    }

    @PostMapping
    public ResponseEntity<BookDetailResponse> createBook(
        @Valid @RequestBody BookCreateRequest request
    ) {
        BookDetailResponse response = bookService.createBook(request);
        return ResponseEntity
            .created(URI.create("/api/books/" + response.id()))
            .body(response);
    }

    @GetMapping("/{id}")
    public BookDetailResponse getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @PatchMapping("/{id}")
    public BookDetailResponse updateBook(
        @PathVariable Long id,
        @Valid @RequestBody BookUpdateRequest request
    ) {
        return bookService.updateBook(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}
