package com.fp.backend.auction.service;

import com.fp.backend.auction.entity.ItemTag;
import com.fp.backend.auction.repository.ItemTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class ItemTagService {

    private final ItemTagRepository itemTagRepository;

    public Optional<ItemTag> findByTagName(String tagName) {
        return itemTagRepository.findByTagName(tagName);
    }

    public ItemTag save(String tagName) {
        return itemTagRepository.save(ItemTag.builder().tagName(tagName).build());
    }
}
