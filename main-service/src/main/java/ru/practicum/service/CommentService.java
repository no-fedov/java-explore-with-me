package ru.practicum.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentStatusUpdateRequest;
import ru.practicum.dto.comment.CommentStatusUpdateResult;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createComment(Long userId, Long eventId, NewCommentDto comment);

    CommentStatusUpdateResult decisionComments(Long userId, Long eventId, CommentStatusUpdateRequest updateCommentDto);

    void deleteComment(Long userId, Long eventId, Long commentId);

    List<CommentDto> getCommentsByEventId(Long userId, Long eventId, Pageable page);

    CommentDto getCommentByIdAndEventId(Long userId, Long eventId, Long commentID);

    CommentDto findCommentById(Long commentId);
}
