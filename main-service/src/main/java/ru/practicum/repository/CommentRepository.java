package ru.practicum.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Comment;
import ru.practicum.repository.custom.CommentPrivateRepository;

import java.time.LocalDateTime;
import java.util.Set;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentPrivateRepository {
    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET c.status = 'APPROVED', c.publishedOn = ?1 WHERE c.id IN(?2)")
    void publishComment(LocalDateTime published, Set<Long> ids);

    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET c.status = 'REJECTED' WHERE c.id IN(?1)")
    void rejectComment(Set<Long> ids);
}
