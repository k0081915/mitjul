package com.mitjul.service;

import com.mitjul.common.error.ApiException;
import com.mitjul.common.error.ErrorCode;
import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.book.BookStatus;
import com.mitjul.domain.user.User;
import com.mitjul.domain.user.UserRepository;
import com.mitjul.dto.book.BookCreateRequest;
import com.mitjul.dto.book.BookDetailResponse;
import com.mitjul.dto.book.BookResponse;
import com.mitjul.dto.book.BookUpdateRequest;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private static final Long SEED_USER_ID = 1L;

    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public List<BookResponse> getBooks(BookStatus status) {
        List<Book> books = status == null
            ? bookRepository.findByUserIdOrderByCreatedAtDesc(SEED_USER_ID)
            : bookRepository.findByUserIdAndStatusOrderByCreatedAtDesc(SEED_USER_ID, status);

        return books.stream()
            .map(BookResponse::from)
            .toList();
    }

    public BookDetailResponse getBook(Long bookId) {
        return BookDetailResponse.from(getBookEntity(bookId));
    }

    @Transactional
    public BookDetailResponse createBook(BookCreateRequest request) {
        validateReadingPeriod(request.startedAt(), request.finishedAt());

        User user = userRepository.findById(SEED_USER_ID)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "기본 사용자를 찾을 수 없습니다."));

        Book book = Book.create(
            user,
            request.title(),
            request.author(),
            request.isbn(),
            request.coverImageUrl(),
            request.presetCoverKey(),
            request.status(),
            request.startedAt(),
            request.finishedAt()
        );

        return BookDetailResponse.from(bookRepository.save(book));
    }

    @Transactional
    public BookDetailResponse updateBook(Long bookId, BookUpdateRequest request) {
        Book book = getBookEntity(bookId);

        LocalDate nextStartedAt = request.startedAt() == null ? book.getStartedAt() : request.startedAt();
        LocalDate nextFinishedAt = request.finishedAt() == null ? book.getFinishedAt() : request.finishedAt();
        validateReadingPeriod(nextStartedAt, nextFinishedAt);

        book.update(
            request.title(),
            request.author(),
            request.isbn(),
            request.coverImageUrl(),
            request.presetCoverKey(),
            request.status(),
            request.startedAt(),
            request.finishedAt()
        );

        return BookDetailResponse.from(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = getBookEntity(bookId);
        bookRepository.delete(book);
    }

    private Book getBookEntity(Long bookId) {
        return bookRepository.findById(bookId)
            .filter(book -> book.getUser().getId().equals(SEED_USER_ID))
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "책을 찾을 수 없습니다."));
    }

    private void validateReadingPeriod(LocalDate startedAt, LocalDate finishedAt) {
        if (startedAt != null && finishedAt != null && finishedAt.isBefore(startedAt)) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "완독일은 시작일보다 빠를 수 없습니다.");
        }
    }
}
