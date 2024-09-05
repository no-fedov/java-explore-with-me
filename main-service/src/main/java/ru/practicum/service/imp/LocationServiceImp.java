package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.dto.LocationDto;
import ru.practicum.dto.mapper.LocationMapper;
import ru.practicum.repository.LocationRepository;
import ru.practicum.service.LocationService;

@Service
@RequiredArgsConstructor
public class LocationServiceImp implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    public void addLocation(LocationDto locationDto) {
        locationRepository.save(LocationMapper.locationFromLocationDto(locationDto));
    }
}
