package com.volzhin.auction.service;

import com.volzhin.auction.entity.Category;
import com.volzhin.auction.repository.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(() -> {
            log.error("Category with id {} not found", id);
            return new EntityNotFoundException(String.format("Category with id %s not found", id));
        });
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
