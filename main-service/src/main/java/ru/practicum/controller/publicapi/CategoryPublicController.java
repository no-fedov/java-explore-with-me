package ru.practicum.controller.publicapi;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

import static ru.practicum.controller.PageConstructor.getPage;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategory(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                         @Positive @RequestParam(defaultValue = "10") Integer size) {
        Pageable page = getPage(from, size);
        return categoryService.getCategoryPage(page);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable("catId") Long id) {
        return categoryService.findCategory(id);
    }
}
