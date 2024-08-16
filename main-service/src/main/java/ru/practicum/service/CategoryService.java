package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import java.util.List;

import static ru.practicum.dto.mapper.CategoryMapper.*;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryDto addCategory(CategoryDto categoryDto) {
        Category category = categoryFromCategoryDto(categoryDto);
        categoryRepository.save(category);
        return categoryDtoFromCategory(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.delete(findCategoryById(id));
    }

    public CategoryDto findCategory(Long id) {
        return categoryDtoFromCategory(findCategoryById(id));
    }

    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = findCategoryById(categoryDto.getId());
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        return categoryDtoFromCategory(category);
    }

    public List<CategoryDto> getCategoryPage(Pageable page) {
        List<Category> category = categoryRepository.findAll(page).stream().toList();
        return categoryDtoListFromCategory(category);
    }
    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException());
    }
}
