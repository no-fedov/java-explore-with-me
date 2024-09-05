package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;
import ru.practicum.repository.custom.CommentPrivateRepository;

import java.time.LocalDateTime;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentPrivateRepository {
    @Modifying
    @Query("UPDATE Comment c SET c.status = 'APPROVED', c.publishedOn = ?1 WHERE c.id IN(?2)")
    int publishComment(LocalDateTime published, Set<Long> ids);

    @Modifying
    @Query("UPDATE Comment c SET c.status = 'REJECTED' WHERE c.id IN(?1)")
    int rejectComment(Set<Long> ids);
}
