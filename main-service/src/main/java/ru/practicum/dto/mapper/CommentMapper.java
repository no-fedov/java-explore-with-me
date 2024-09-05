package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.model.Comment;
import ru.practicum.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment convertToCommentFromNewCommentDto(NewCommentDto dto) {
        return Comment.builder()
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
                .updatedOn(comment.getUpdatedOn())
                .status(comment.getStatus())
                .build();
    }
}
