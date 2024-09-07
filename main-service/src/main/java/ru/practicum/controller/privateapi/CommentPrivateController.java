package ru.practicum.controller.privateapi;

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
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

import static ru.practicum.controller.PageConstructor.getPage;

@RestController
@RequestMapping("/users/{userId}/event/{eventId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PositiveOrZero @PathVariable Long userId,
                                    @PositiveOrZero @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto comment) {
        CommentDto createdComment = commentService.createComment(userId, eventId, comment);
        log.info("POST /users/{userId}/event/{eventId}/comments userId = {} eventId = {} createdComment = {}", userId,
                eventId, createdComment);
        return createdComment;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentStatusUpdateResult decisionComments(@PositiveOrZero @PathVariable Long userId,
                                                      @PositiveOrZero @PathVariable Long eventId,
                                                      @Valid @RequestBody CommentStatusUpdateRequest commentStatusUpdateRequest) {
        CommentStatusUpdateResult decision = commentService.decisionComments(userId, eventId, commentStatusUpdateRequest);
        log.info("PATCH /users/{userId}/event/{eventId}/comments userId = {} eventId = {} patchedComment = {}", userId,
                eventId, decision);
        return decision;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PositiveOrZero @PathVariable Long userId,
                              @PositiveOrZero @PathVariable Long eventId,
                              @PositiveOrZero @PathVariable Long commentId) {
        log.info("DELETE /users/{userId}/event/{eventId}/comments/{commentId} userId = {}," +
                " eventId = {}, commentId = {}", userId, eventId, commentId);
        commentService.deleteComment(userId, eventId, commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsByEventId(@PositiveOrZero @PathVariable Long userId,
                                                 @PositiveOrZero @PathVariable Long eventId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer size) {
        List<CommentDto> commentsByEventId = commentService.getCommentsByEventId(userId, eventId, getPage(from, size));
        log.info("GET /users/{userId}/event/{eventId}/comments userId = {} eventId = {} result = {}", userId,
                eventId, commentsByEventId);
        return commentsByEventId;
    }

    @GetMapping("/{commentId}")
    public CommentDto findComment(@PositiveOrZero @PathVariable Long userId,
                                  @PositiveOrZero @PathVariable Long eventId,
                                  @PositiveOrZero @PathVariable Long commentId) {
        log.info("GET /users/{userId}/event/{eventId}/comments/{commentId}" +
                " userId = {} eventId = {} commentId = {}", userId, eventId, commentId);
        CommentDto commentByIdAndEventId = commentService.getCommentByIdAndEventId(userId, eventId, commentId);
        log.info("Найденный комментарий {}", commentByIdAndEventId);
        return commentByIdAndEventId;
    }
}