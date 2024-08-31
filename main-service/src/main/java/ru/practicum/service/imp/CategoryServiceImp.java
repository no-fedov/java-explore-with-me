package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.CategoryDto;
import ru.practicum.exception.CategoryActionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.service.CategoryService;

import java.util.List;
import java.util.Optional;

import static ru.practicum.dto.mapper.CategoryMapper.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImp implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {
        checkNameDuplication(categoryDto.getName());
        Category category = categoryFromCategoryDto(categoryDto);
        categoryRepository.save(category);
        log.info("saved category: {}", category);
        return categoryDtoFromCategory(category);
    }

    @Override
    public void deleteCategory(Long id) {
        log.info("delete category where id = {}", id);
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryDto findCategory(Long id) {
        log.info("search Category where id = {}", id);
        return categoryDtoFromCategory(findCategoryById(id));
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Category category = findCategoryById(categoryDto.getId());
        checkNameDuplication(categoryDto.getName());
        category.setName(categoryDto.getName());
        categoryRepository.save(category);
        log.info("updated Category: {}", category);
        return categoryDtoFromCategory(category);
    }

    @Override
    public List<CategoryDto> getCategoryPage(Pageable page) {
        List<Category> category = categoryRepository.findAll(page).stream().toList();
        log.info("found Category: {}", category);
        return categoryDtoListFromCategory(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Такой категории не найдено"));
    }

    private void checkNameDuplication(String name) {
        Optional<Category> categoryByName = categoryRepository.findByName(name);
        if (categoryByName.isPresent()) {
            throw new CategoryActionException("Дублирование имени категории");
        }
    }
}
