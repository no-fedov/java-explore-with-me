package ru.practicum.controller.adminapi;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.service.CommentService;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getCommentById(@PositiveOrZero @PathVariable Long commentId) {
        CommentDto commentById = commentService.findCommentById(commentId);
        log.info("GET /admin/comments/{commentId} commentId = {} result = {}", commentById, commentById);
        return commentById;
    }
}
