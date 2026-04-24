package com.mitjul.api;

import com.mitjul.dto.tag.TagResponse;
import com.mitjul.service.TagService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @GetMapping
    public List<TagResponse> getTags() {
        return tagService.getTags();
    }
}
