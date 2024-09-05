package ru.practicum.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentStatusUpdateRequest;
import ru.practicum.dto.comment.CommentStatusUpdateResult;
import ru.practicum.dto.comment.NewCommentDto;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto comment);

    CommentStatusUpdateResult decisionComments(Long userId, Long eventId, CommentStatusUpdateRequest updateCommentDto);

    void deleteComment(Long userId, Long commentId);
}
