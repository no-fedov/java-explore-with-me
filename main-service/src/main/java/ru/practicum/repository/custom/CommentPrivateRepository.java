package ru.practicum.repository.custom;

import org.springframework.data.domain.Pageable;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.model.Comment;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CommentPrivateRepository {
    List<CommentDto> getCommentsByIds(Set<Long> ids, Long eventId);

    Optional<Comment> findCommentForEventInitiatorOrAuthor(Long userId, Long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Pageable page);

    Optional<CommentDto> findCommentById(Long commentId);
}
