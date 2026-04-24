package com.mitjul.service;

import com.mitjul.common.error.ApiException;
import com.mitjul.common.error.ErrorCode;
import com.mitjul.domain.book.Book;
import com.mitjul.domain.book.BookRepository;
import com.mitjul.domain.quote.QuoteCard;
import com.mitjul.domain.quote.QuoteCardRepository;
import com.mitjul.domain.tag.Tag;
import com.mitjul.domain.tag.TagRepository;
import com.mitjul.dto.quote.QuoteCardCreateRequest;
import com.mitjul.dto.quote.QuoteCardResponse;
import com.mitjul.dto.quote.QuoteCardUpdateRequest;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuoteCardService {

    private static final Long SEED_USER_ID = 1L;

    private final QuoteCardRepository quoteCardRepository;
    private final BookRepository bookRepository;
    private final TagRepository tagRepository;

    public List<QuoteCardResponse> getQuotesByBook(Long bookId) {
        ensureBookExists(bookId);
        return quoteCardRepository.findByBookIdOrderByCreatedAtDesc(bookId)
            .stream()
            .map(QuoteCardResponse::from)
            .toList();
    }

    public List<QuoteCardResponse> searchQuotes(String keyword, Long bookId, String tagName) {
        String normalizedKeyword = normalizeBlankToNull(keyword);
        String normalizedTagName = normalizeBlankToNull(tagName);

        if (bookId != null) {
            ensureBookExists(bookId);
        }
        if (normalizedTagName != null && tagRepository.findByName(normalizedTagName).isEmpty()) {
            return List.of();
        }

        return quoteCardRepository.search(normalizedKeyword, bookId, normalizedTagName)
            .stream()
            .filter(quoteCard -> quoteCard.getBook().getUser().getId().equals(SEED_USER_ID))
            .map(QuoteCardResponse::from)
            .toList();
    }

    @Transactional
    public QuoteCardResponse createQuote(Long bookId, QuoteCardCreateRequest request) {
        Book book = ensureBookExists(bookId);
        List<Tag> tags = getTagsByNames(request.tagNames());

        QuoteCard quoteCard = QuoteCard.create(
            book,
            request.page(),
            request.content(),
            request.memo(),
            tags
        );

        return QuoteCardResponse.from(quoteCardRepository.save(quoteCard));
    }

    @Transactional
    public QuoteCardResponse updateQuote(Long quoteId, QuoteCardUpdateRequest request) {
        QuoteCard quoteCard = getQuoteEntity(quoteId);
        List<Tag> tags = request.tagNames() == null ? null : getTagsByNames(request.tagNames());

        quoteCard.update(
            request.page(),
            request.content(),
            request.memo(),
            tags
        );

        return QuoteCardResponse.from(quoteCard);
    }

    @Transactional
    public void deleteQuote(Long quoteId) {
        QuoteCard quoteCard = getQuoteEntity(quoteId);
        quoteCardRepository.delete(quoteCard);
    }

    private Book ensureBookExists(Long bookId) {
        return bookRepository.findById(bookId)
            .filter(book -> book.getUser().getId().equals(SEED_USER_ID))
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "책을 찾을 수 없습니다."));
    }

    private QuoteCard getQuoteEntity(Long quoteId) {
        return quoteCardRepository.findById(quoteId)
            .filter(quoteCard -> quoteCard.getBook().getUser().getId().equals(SEED_USER_ID))
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND, "인용문을 찾을 수 없습니다."));
    }

    private List<Tag> getTagsByNames(List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return List.of();
        }

        Set<String> uniqueNames = new LinkedHashSet<>(tagNames);
        List<Tag> tags = tagRepository.findByNameIn(uniqueNames);
        if (tags.size() != uniqueNames.size()) {
            throw new ApiException(ErrorCode.BAD_REQUEST, "존재하지 않는 태그가 포함되어 있습니다.");
        }

        return tags;
    }

    private String normalizeBlankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
