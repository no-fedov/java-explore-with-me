package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CategoryDto;
import ru.practicum.service.CategoryService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/categories")
@Slf4j
public class CategoryAdminController {
    private final CategoryService categoryServiceImp;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info("POST /admin/categories {}", categoryDto);
        return categoryServiceImp.addCategory(categoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long id) {
        log.info("DELETE /{catId} catId = {}", id);
        categoryServiceImp.deleteCategory(id);
    }

    @GetMapping("/{catId}")
    public CategoryDto findCategory(@PathVariable("catId") Long id) {
        log.info("GET /{catId} catId = {}",id);
        return categoryServiceImp.findCategory(id);
    }

    @PatchMapping("/{catId}")
    public CategoryDto patchCategory(@PathVariable("catId") Long id,
                                     @Valid @RequestBody CategoryDto categoryDto) {
        categoryDto.setId(id);
        log.info("PATCH /{catId} catId = {}; body = {}", id, categoryDto);
        return categoryServiceImp.updateCategory(categoryDto);
    }
}