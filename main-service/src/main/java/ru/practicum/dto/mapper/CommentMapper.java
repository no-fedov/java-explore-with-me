package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentStatusUpdateResult;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment convertToCommentFromNewCommentDto(NewCommentDto dto, User user, Event event) {
        return Comment.builder()
                .author(user)
                .event(event)
                .description(dto.getDescription())
                .build();
    }

    public static CommentDto convertToCommentDtoFromComment(Long eventId, User user, Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .user(UserMapper.convertToUserShortDtoFromUser(user))
                .description(comment.getDescription())
                .eventId(eventId)
                .publishedOn(comment.getPublishedOn())
                .status(comment.getStatus())
                .build();
    }

    public static CommentStatusUpdateResult concertToUpdatedResult(List<CommentDto> list) {
        return CommentStatusUpdateResult.builder()
                .resolvedComments(list)
                .build();
    }
}
