package com.mitjul.dto.quote;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record QuoteCardUpdateRequest(
    @Positive(message = "페이지 번호는 1 이상이어야 합니다.")
    Integer page,

    String content,

    String memo,

    @Size(max = 8, message = "태그는 최대 8개까지 선택할 수 있습니다.")
    List<@Size(max = 30, message = "태그 이름은 30자 이하여야 합니다.") String> tagNames
) {
}
