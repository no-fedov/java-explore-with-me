package ru.practicum.controller.admin_api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryServiceImp;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@Valid @RequestBody CategoryDto categoryDto) {
        return categoryServiceImp.addCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long id) {
        categoryServiceImp.deleteCategory(id);
    }

    @GetMapping("/{catId}")
    public CategoryDto findCategory(@PathVariable("catId") Long id) {
        return categoryServiceImp.findCategory(id);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable("catId") Long id,
                                     @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(id);
        return categoryServiceImp.updateCategory(categoryDto);
    }
}