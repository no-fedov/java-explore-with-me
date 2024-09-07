package ru.practicum.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.model.status.CommentStatus;

import java.time.LocalDateTime;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_author", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "fk_event", nullable = false)
    private Event event;
    private String description;
    @Column(name = "publishedOn")
    private LocalDateTime publishedOn;
    @Enumerated(EnumType.STRING)
    private CommentStatus status;
}
