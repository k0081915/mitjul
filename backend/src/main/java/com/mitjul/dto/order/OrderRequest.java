package com.mitjul.dto.order;

import com.mitjul.domain.order.CoverStyle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record OrderRequest(
    @NotNull(message = "시작일은 필수입니다.")
    LocalDate periodStart,

    @NotNull(message = "종료일은 필수입니다.")
    LocalDate periodEnd,

    @NotNull(message = "표지 스타일은 필수입니다.")
    CoverStyle coverStyle,

    @NotBlank(message = "소유자 이름은 필수입니다.")
    @Size(max = 50, message = "소유자 이름은 50자 이하여야 합니다.")
    String ownerName
) {
}
