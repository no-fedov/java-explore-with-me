package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class EventShortDto {
    private Long id;
    private UserShortDto initiator;
    private CategoryDto category;
    private LocationDto location;
    @NotBlank
    @Size(min = 3, max = 120)
    private String title;
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;
    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;
    private Long participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime eventDate;
    private Boolean paid;
    private Boolean requestModeration;
    private StateEvent state;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
}
