package ru.practicum.dto.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
