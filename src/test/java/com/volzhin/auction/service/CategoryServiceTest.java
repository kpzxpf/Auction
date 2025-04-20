package com.volzhin.auction.service;

import com.volzhin.auction.entity.Category;
import com.volzhin.auction.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getCategoryByName_shouldReturnCategory_whenFound() {
        // Arrange
        String categoryName = "Electronics";
        Category expectedCategory = Category.builder()
                .id(1L)
                .name(categoryName)
                .build();
        when(categoryRepository.findByName(categoryName)).thenReturn(expectedCategory);

        // Act
        Category actualCategory = categoryService.getCategoryByName(categoryName);

        // Assert
        assertNotNull(actualCategory);
        assertEquals(expectedCategory.getId(), actualCategory.getId());
        assertEquals(expectedCategory.getName(), actualCategory.getName());
        verify(categoryRepository).findByName(categoryName);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getCategoryByName_shouldReturnNull_whenNotFound() {
        // Arrange
        String categoryName = "NonExistentCategory";
        when(categoryRepository.findByName(categoryName)).thenReturn(null);

        // Act
        Category actualCategory = categoryService.getCategoryByName(categoryName);

        // Assert
        assertNull(actualCategory);
        verify(categoryRepository).findByName(categoryName);
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getAllCategories_shouldReturnListOfCategories() {
        // Arrange
        Category category1 = Category.builder().id(1L).name("Books").build();
        Category category2 = Category.builder().id(2L).name("Art").build();
        List<Category> expectedCategories = List.of(category1, category2);

        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<Category> actualCategories = categoryService.getAllCategories();

        // Assert
        assertNotNull(actualCategories);
        assertEquals(2, actualCategories.size());
        assertEquals(expectedCategories, actualCategories);
        verify(categoryRepository).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    void getAllCategories_shouldReturnEmptyList_whenNoCategoriesExist() {
        // Arrange
        List<Category> expectedCategories = List.of(); // Пустой список

        when(categoryRepository.findAll()).thenReturn(expectedCategories);

        // Act
        List<Category> actualCategories = categoryService.getAllCategories();

        // Assert
        assertNotNull(actualCategories);
        assertTrue(actualCategories.isEmpty());
        verify(categoryRepository).findAll();
        verifyNoMoreInteractions(categoryRepository);
    }
}