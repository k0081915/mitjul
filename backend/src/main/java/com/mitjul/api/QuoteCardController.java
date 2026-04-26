package com.mitjul.api;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mitjul.dto.quote.QuoteCardCreateRequest;
import com.mitjul.dto.quote.QuoteCardResponse;
import com.mitjul.dto.quote.QuoteCardUpdateRequest;
import com.mitjul.service.QuoteCardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class QuoteCardController {

    private final QuoteCardService quoteCardService;

    @GetMapping("/api/books/{bookId}/quotes")
    public List<QuoteCardResponse> getQuotesByBook(@PathVariable Long bookId) {
        return quoteCardService.getQuotesByBook(bookId);
    }

    @PostMapping("/api/books/{bookId}/quotes")
    public ResponseEntity<QuoteCardResponse> createQuote(
        @PathVariable Long bookId,
        @Valid @RequestBody QuoteCardCreateRequest request
    ) {
        QuoteCardResponse response = quoteCardService.createQuote(bookId, request);
        return ResponseEntity
            .created(URI.create("/api/quotes/" + response.id()))
            .body(response);
    }

    @PatchMapping("/api/quotes/{id}")
    public QuoteCardResponse updateQuote(
        @PathVariable Long id,
        @Valid @RequestBody QuoteCardUpdateRequest request
    ) {
        return quoteCardService.updateQuote(id, request);
    }

    @DeleteMapping("/api/quotes/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable Long id) {
        quoteCardService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/quotes/search")
    public List<QuoteCardResponse> searchQuotes(
        @RequestParam(required = false, name = "q") String keyword,
        @RequestParam(required = false) String tag,
        @RequestParam(required = false) Long bookId
    ) {
        return quoteCardService.searchQuotes(keyword, bookId, tag);
    }
}
