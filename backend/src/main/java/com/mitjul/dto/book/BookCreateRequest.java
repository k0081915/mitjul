package com.mitjul.dto.book;

import com.mitjul.domain.book.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record BookCreateRequest(
    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    String title,

    @NotBlank(message = "저자는 필수입니다.")
    @Size(max = 100, message = "저자는 100자 이하여야 합니다.")
    String author,

    @Size(max = 20, message = "ISBN은 20자 이하여야 합니다.")
    String isbn,

    @Size(max = 500, message = "표지 이미지 URL은 500자 이하여야 합니다.")
    String coverImageUrl,

    @Size(max = 50, message = "표지 프리셋 키는 50자 이하여야 합니다.")
    String presetCoverKey,

    BookStatus status,

    @NotNull(message = "시작일은 필수입니다.")
    @PastOrPresent(message = "시작일은 오늘 또는 과거 날짜여야 합니다.")
    LocalDate startedAt,

    @PastOrPresent(message = "완독일은 오늘 또는 과거 날짜여야 합니다.")
    LocalDate finishedAt
) {
}
