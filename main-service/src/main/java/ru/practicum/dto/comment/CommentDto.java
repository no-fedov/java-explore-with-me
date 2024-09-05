package ru.practicum.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.status.CommentStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CommentDto {
    private Long id;
    private Long eventId;
    private UserShortDto user;
    private String description;
    private LocalDateTime publishedOn;
    private LocalDateTime updatedOn;
    private CommentStatus status;
}
