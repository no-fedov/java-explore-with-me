package ru.practicum.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class UpdateEventAdminRequest extends UpdateEvent {
    private StateActionAdmin stateAction;

    public enum StateActionAdmin {
        PUBLISH_EVENT,
        REJECT_EVENT;
    }
}