package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.CommentStatusUpdateRequest;
import ru.practicum.dto.comment.CommentStatusUpdateResult;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.mapper.CommentMapper;
import ru.practicum.exception.CommentActionException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;
import ru.practicum.model.status.CommentStatus;
import ru.practicum.model.status.RequestStatus;
import ru.practicum.model.status.StateEvent;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.dto.comment.CommentStatusUpdateRequest.Status.APPROVE;

@Service
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImp.class);
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto comment) {
        User currentUser = getCurrentUser(userId);
        Event currentEvent = getCurrentEvent(eventId);

        if (currentEvent.getState() != StateEvent.PUBLISHED) {
            throw new CommentActionException("Нельзя комментировать неопубликованные события");
        }

        CommentStatus status = null;
        LocalDateTime publishedTime = null;

        if (currentEvent.getInitiator().getId().equals(currentUser.getId())) {
            status = CommentStatus.APPROVED;
            publishedTime = LocalDateTime.now();
        } else {
            if (!hasConfirmedRequest(userId, eventId)) {
                throw new CommentActionException("Пользователь не может оставить комментарий, " +
                        "так как у него нет подтвержденных заявок на участие в событии");
            }
            status = CommentStatus.PENDING;
        }

        Comment newComment = CommentMapper.convertToCommentFromNewCommentDto(comment, currentUser, currentEvent);

        newComment.setStatus(status);
        newComment.setPublishedOn(publishedTime);
        commentRepository.save(newComment);
        CommentDto addedCommentDto = CommentMapper.convertToCommentDtoFromComment(eventId, currentUser, newComment);
        log.info("Добавлен комментарий: {}", addedCommentDto);
        return addedCommentDto;
    }

    @Override
    public CommentStatusUpdateResult decisionComments(Long eventId,
                                                      CommentStatusUpdateRequest commentStatusUpdateRequest) {
        getCurrentEvent(eventId);

        List<CommentDto> comments = commentRepository.getCommentsByIds(commentStatusUpdateRequest.getIds(), eventId);

        if (commentStatusUpdateRequest.getStatus() == APPROVE
                && comments.stream().anyMatch(c -> c.getStatus() == CommentStatus.APPROVED)) {
            throw new CommentActionException("Нельзя дважды одобрить публикацию комментария");
        }

        if (commentStatusUpdateRequest.getStatus() == CommentStatusUpdateRequest.Status.REJECT
                && comments.stream().anyMatch(c -> c.getStatus() == CommentStatus.REJECTED)) {
            throw new CommentActionException("Нельзя дважды отклонить публикацию комментария");
        }

        CommentStatus updatedStatus = commentStatusUpdateRequest.getStatus() == APPROVE
                ? CommentStatus.APPROVED
                : CommentStatus.REJECTED;

        Set<Long> commentsIds = comments.stream()
                .peek(c -> c.setStatus(updatedStatus))
                .map(CommentDto::getId).collect(Collectors.toSet());

        if (updatedStatus == CommentStatus.APPROVED) {
            commentRepository.publishComment(LocalDateTime.now(), commentsIds);
        } else {
            commentRepository.rejectComment(commentsIds);
        }
        CommentStatusUpdateResult commentStatusUpdateResult = CommentMapper.concertToUpdatedResult(comments);
        log.info("Принято решение по следующим комментариям: {}\nРезультат: {}", commentStatusUpdateResult, commentStatusUpdateResult);
        return commentStatusUpdateResult;
    }

    @Override
    public void deleteComment(Long userId, Long eventId, Long commentId) {
        getCurrentUser(userId);
        getCurrentEvent(eventId);
        Comment currentComment = commentRepository.findCommentForEventInitiatorOrAuthor(userId, commentId)
                .orElseThrow(() -> new CommentActionException("Комментарий может удалить только инициатор события" +
                        " или автор комментария"));
        commentRepository.deleteById(commentId);
        log.info("Выполнено удаление комментария: {}", currentComment);
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long userId, Long eventId, Pageable page) {
        User currentUser = getCurrentUser(userId);
        Event currentEvent = getCurrentEvent(eventId);
        if (!currentUser.getId().equals(currentEvent.getInitiator().getId())) {
            throw new CommentActionException("Нельзя просмотреть все комментариии если вы не владаелец события");
        }
        List<CommentDto> commentsByEventId = commentRepository.getCommentsByEventId(eventId, page);
        log.info("Найдены комментарии: {}", commentsByEventId);
        return commentsByEventId;
    }

    @Override
    public CommentDto getCommentByIdAndEventId(Long userId, Long eventId, Long commentID) {
        getCurrentUser(userId);
        getCurrentEvent(eventId);
        List<CommentDto> currentComments = commentRepository.getCommentsByIds(Set.of(commentID), eventId);

        if (currentComments.isEmpty()) {
            throw new NotFoundException("Комментарий не найден");
        }

        CommentDto currentComment = currentComments.getFirst();
        log.info("Найден комментарий: {}", currentComment);
        return currentComment;
    }

    @Override
    public CommentDto findCommentById(Long commentId) {
        CommentDto commentDto = commentRepository.findCommentById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не существует"));
        log.info("Найден комментарий по id: {}", commentDto);
        return commentDto;
    }

    private User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
    }

    private Event getCurrentEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Такого события нет"));
    }

    private boolean hasConfirmedRequest(Long userId, Long eventId) {
        return requestRepository.existsByRequester_IdAndEvent_IdAndStatusIn(userId,
                eventId,
                Set.of(RequestStatus.CONFIRMED));
    }
}