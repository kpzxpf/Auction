package com.volzhin.auction.mapper;

import com.volzhin.auction.dto.CategoryDto;
import com.volzhin.auction.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    CategoryDto toDto(Category category);

    Category toEntity(CategoryDto dto);

    List<CategoryDto> toDto(List<Category> categories);

    List<Category> toEntity(List<CategoryDto> dtos);
}
