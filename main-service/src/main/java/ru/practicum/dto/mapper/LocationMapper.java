package ru.practicum.dto.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.dto.LocationDto;
import ru.practicum.model.Location;
import ru.practicum.model.LocationId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static Location locationFromLocationDto(LocationDto locationDto) {
        return Location.builder()
                .locationId(LocationId.builder()
                        .lat(locationDto.getLat())
                        .lon(locationDto.getLon())
                        .build())
                .build();

    }


    public static LocationDto locationDtoFromLocation(Location location) {
        LocationId locationId = location.getLocationId();
        return LocationDto.builder()
                .lat(locationId.getLat())
                .lon(locationId.getLon())
                .build();
    }
}
