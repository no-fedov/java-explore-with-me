package ru.practicum.dto.comment;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class CommentStatusUpdateRequest {
    @NotEmpty
    private Set<Long> ids;
    @NotNull
    private Status status;

    public enum Status {
        APPROVE,
        REJECT
    }
}
