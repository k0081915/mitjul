package com.mitjul.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequest(
    @NotNull(message = "별점은 필수입니다.")
    @Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5점 이하여야 합니다.")
    Byte rating,

    @NotBlank(message = "한줄평은 필수입니다.")
    @Size(max = 200, message = "한줄평은 200자 이하여야 합니다.")
    String oneLiner,

    @NotBlank(message = "리뷰 본문은 필수입니다.")
    String body,

    Boolean markCompleted
) {
}
