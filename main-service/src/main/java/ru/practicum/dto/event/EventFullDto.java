package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.status.StateEvent;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    private Long id;
    private UserShortDto initiator;
    private CategoryDto category;
    private LocationDto location;
    private String title;
    private String annotation;
    private String description;
    private Long participantLimit;
    private Long confirmedRequests;
    private Boolean paid;

    private Boolean requestModeration;
    private StateEvent state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private Long views;
}
