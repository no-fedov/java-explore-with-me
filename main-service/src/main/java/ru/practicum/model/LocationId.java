package ru.practicum.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LocationId implements Serializable {
    private String lat;
    private String lon;
}
