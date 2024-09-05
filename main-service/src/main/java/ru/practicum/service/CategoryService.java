package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(CategoryDto categoryDto);

    void deleteCategory(Long id);

    CategoryDto findCategory(Long id);

    CategoryDto updateCategory(CategoryDto categoryDto);

    List<CategoryDto> getCategoryPage(Pageable page);
}