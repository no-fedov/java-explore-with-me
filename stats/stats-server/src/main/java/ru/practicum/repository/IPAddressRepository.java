package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.IPAddress;

import java.util.Optional;

public interface IPAddressRepository extends JpaRepository<IPAddress, Long> {
    Optional<IPAddress> findByAddress(String address);
}
