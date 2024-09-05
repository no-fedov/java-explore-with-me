package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import ru.practicum.dto.LocationDto;

import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public abstract class UpdateEvent {
    private Long id;
    private Long initiator;
    @Positive
    private Long category;
    @Size(min = 3, max = 120)
    private String title;
    @Size(min = 20, max = 2000)
    private String annotation;
    @Size(min = 20, max = 7000)
    private String description;
    @PositiveOrZero
    private Long participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    @Valid
    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;
}