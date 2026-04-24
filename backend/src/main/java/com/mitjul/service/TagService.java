package com.mitjul.service;

import com.mitjul.domain.tag.TagRepository;
import com.mitjul.dto.tag.TagResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public List<TagResponse> getTags() {
        return tagRepository.findAllByOrderByIdAsc()
            .stream()
            .map(TagResponse::from)
            .toList();
    }
}
