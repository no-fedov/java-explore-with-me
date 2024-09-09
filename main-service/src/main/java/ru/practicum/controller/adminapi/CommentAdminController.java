package ru.practicum.controller.adminapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentStatusUpdateRequest;
import ru.practicum.dto.comment.CommentStatusUpdateResult;
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

    @PatchMapping("/event/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentStatusUpdateResult decisionComments(@PositiveOrZero @PathVariable Long eventId,
                                                      @Valid @RequestBody CommentStatusUpdateRequest commentStatusUpdateRequest) {
        CommentStatusUpdateResult decision = commentService.decisionComments(eventId, commentStatusUpdateRequest);
        log.info("PATCH /admin/comments/event/{eventId}eventId = {} patchedComment = {}", eventId, decision);
        return decision;
    }
}