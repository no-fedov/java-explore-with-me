package ru.practicum.repository.custom.imp;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        List<CommentDto> commentDtoList = queryTemplateForCommentDto()
                .where(comment.event.id.eq(eventId))
                .where(comment.id.in(ids))
                .fetch();
        return commentDtoList == null ? List.of() : commentDtoList;
    }

    @Override
    public Optional<Comment> findCommentForEventInitiatorOrAuthor(Long userId, Long commentId) {
        return Optional.ofNullable(queryTemplateForOwnerEventOrComment(userId, commentId).fetchOne());
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Pageable page) {
        return queryTemplateForCommentDto()
                .where(comment.event.id.eq(eventId))
                .offset(page.getOffset())
                .limit(page.getPageSize()).fetch();
    }

    @Override
    public Optional<CommentDto> findCommentById(Long commentId) {
        return Optional.ofNullable(queryTemplateForCommentDto()
                .where(comment.id.eq(commentId))
                .fetchOne());
    }

    private JPAQuery<Comment> queryTemplateForOwnerEventOrComment(Long userId, Long commentId) {
        return queryFactory.select(comment)
                .from(comment)
                .where(comment.author.id.eq(userId)
                        .or(comment.id.eq(commentId).and(comment.id.eq(commentId))));
    }

    private JPAQuery<CommentDto> queryTemplateForCommentDto() {
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
                .leftJoin(comment.author, user);
    }
}
