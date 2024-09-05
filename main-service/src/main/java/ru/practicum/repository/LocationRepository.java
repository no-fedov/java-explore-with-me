package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Location;
import ru.practicum.model.LocationId;

public interface LocationRepository extends JpaRepository<Location, LocationId> {
}
