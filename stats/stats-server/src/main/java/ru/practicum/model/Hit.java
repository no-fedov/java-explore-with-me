package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hits")
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_app", nullable = false)
    private Application app;

    @ManyToOne
    @JoinColumn(name = "fk_ip", nullable = false)
    private IPAddress ip;
    private String uri;
    private LocalDateTime time;
}
