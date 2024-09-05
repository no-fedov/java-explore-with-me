package ru.practicum.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.model.status.CommentStatus;

import java.time.LocalDateTime;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@Setter
@Getter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_author")
    private User author;

    @ManyToOne
    @JoinColumn(name = "fk_event")
    private Event event;
    private String description;
    @Column(name = "publishedOn")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
}
