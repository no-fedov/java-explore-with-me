package ru.practicum.controller.privateapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentStatusUpdateRequest;
import ru.practicum.dto.comment.CommentStatusUpdateResult;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PositiveOrZero @PathVariable Long userId,
                                    @PositiveOrZero @PathVariable Long eventId,
                                    @Valid @RequestBody NewCommentDto comment) {
        CommentDto createdComment = commentService.createComment(userId, eventId, comment);
        return createdComment;
    }

    @PatchMapping("/{eventId}")
    public CommentStatusUpdateResult decisionComments(@PositiveOrZero @PathVariable Long userId,
                                                      @PositiveOrZero @PathVariable Long eventId,
                                                      @Valid @RequestBody CommentStatusUpdateRequest commentStatusUpdateRequest) {
        CommentStatusUpdateResult decision = commentService.decisionComments(userId, eventId, commentStatusUpdateRequest);
        return decision;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PositiveOrZero @PathVariable Long userId,
                              @PositiveOrZero @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<Comment> getCommentById(@PathVariable Long id) {
//        Comment comment = commentService.getCommentById(id);
//        if (comment != null) {
//            return ResponseEntity.ok(comment);
//        } else {
//            return ResponseEntity.notFound().build();
//        }
//    }

}
