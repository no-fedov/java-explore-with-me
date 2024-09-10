package ru.practicum.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
public class CommentStatusUpdateResult {
    private List<CommentDto> resolvedComments;
}