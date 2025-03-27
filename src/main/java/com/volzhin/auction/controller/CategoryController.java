package com.volzhin.auction.controller;

import com.volzhin.auction.dto.CategoryDto;
import com.volzhin.auction.mapper.CategoryMapper;
import com.volzhin.auction.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryMapper.toDto(categoryService.getAllCategories());
    }
}