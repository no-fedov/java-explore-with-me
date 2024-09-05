package ru.practicum.repository.custom.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.user.UserShortDto;
import ru.practicum.model.Comment;
import ru.practicum.repository.custom.CommentPrivateRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static ru.practicum.model.QComment.comment;
import static ru.practicum.model.QUser.user;

@Repository
@RequiredArgsConstructor
public class CommentPrivateRepositoryImpl implements CommentPrivateRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDto> getCommentsByIds(Set<Long> ids, Long eventId) {
        return queryTemplateForCommentsByIdsAndEventId(ids, eventId).fetch();
    }

    @Override
    public Optional<Comment> findCommentForEventInitiatorOrAuthor(Long userId, Long commentId) {
        return Optional.ofNullable(queryTemplateForOwnerEventOrComment(userId, commentId).fetchOne());
    }

    private JPAQuery<CommentDto> queryTemplateForCommentsByIdsAndEventId(Set<Long> ids, Long eventId) {
        return queryFactory.select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.event.id,
                        Projections.constructor(UserShortDto.class,
                                comment.author.id,
                                comment.author.name),
                        comment.description,
                        comment.publishedOn,
                        comment.status)
                ).from(comment)
                .leftJoin(user)
                .where(comment.event.id.eq(eventId).and(comment.id.in(ids)));
    }

    private JPAQuery<Comment> queryTemplateForOwnerEventOrComment(Long userId, Long commentId) {
        return queryFactory.select(comment)
                .from(comment)
                .where(comment.author.id.eq(userId)
                        .or(comment.id.eq(commentId).and(comment.id.eq(commentId))));
    }
}
