package com.mitjul.dto.tag;

import com.mitjul.domain.tag.Tag;

public record TagResponse(
    Long id,
    String name
) {

    public static TagResponse from(Tag tag) {
        return new TagResponse(tag.getId(), tag.getName());
    }
}
