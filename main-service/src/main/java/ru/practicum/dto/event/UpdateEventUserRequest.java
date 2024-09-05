package ru.practicum.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class UpdateEventUserRequest extends UpdateEvent {
    private StateActionUser stateAction;

    public enum StateActionUser {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}