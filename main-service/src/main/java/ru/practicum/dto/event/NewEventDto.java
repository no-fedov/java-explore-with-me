package ru.practicum.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.dto.LocationDto;
import ru.practicum.exception.NoValidParameter;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class NewEventDto {
    @PositiveOrZero
    private Long category;

    @NotBlank
    @Size(min = 3, max = 120)
    private String title;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @PositiveOrZero
    private Long participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime eventDate;

    private LocationDto location;
    private Boolean paid;
    private Boolean requestModeration;

    public void checkValid() {
        if (eventDate != null && eventDate.isBefore(LocalDateTime.now().minusHours(2))) {
            throw new NoValidParameter("дата и время на которые намечено событие не может быть раньше," +
                    " чем через два часа от текущего момента");
        }
    }
}
