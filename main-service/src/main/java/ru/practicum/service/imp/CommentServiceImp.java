package ru.practicum.service.imp;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.model.status.CommentStatus;
import ru.practicum.model.status.RequestStatus;
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
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto createComment(Long userId, Long eventId, NewCommentDto comment) {
        User currentUser = getCurrentUser(userId);
        getCurrentEvent(eventId);
        getCurrentRequest(userId, eventId);
        Comment newComment = CommentMapper.convertToCommentFromNewCommentDto(comment);
        newComment.setStatus(CommentStatus.PENDING);
        commentRepository.save(newComment);
        return CommentMapper.convertToCommentDtoFromComment(eventId, currentUser, newComment);
    }

    @Override
    public CommentStatusUpdateResult decisionComments(Long userId,
                                                      Long eventId,
                                                      CommentStatusUpdateRequest commentStatusUpdateRequest) {
        getCurrentUser(userId);
        getCurrentEventForInitiator(userId, eventId);

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
        return CommentMapper.concertToUpdatedResult(comments);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        getCurrentUser(userId);
        Comment currentComment = commentRepository.findCommentForEventInitiatorOrAuthor(userId, commentId)
                .orElseThrow(() -> new CommentActionException("Ошибка при удалении комментария"));
        commentRepository.delete(currentComment);
    }

    private User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
    }

    private Event getCurrentEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Такого события нет"));
    }

    private Event getCurrentEventForInitiator(Long userId, Long eventId) {
        return eventRepository.findByIdAndInitiator_Id(eventId, userId)
                .orElseThrow(() -> new NotFoundException("У пользователя нет такого события"));
    }

    private Request getCurrentRequest(Long userId, Long eventId) {
        return requestRepository.findByRequester_IdAndEvent_IdAndStatusIn(userId, eventId, Set.of(RequestStatus.CONFIRMED))
                .orElseThrow(() -> new CommentActionException("Пользователь не может оставить комментарий, " +
                        "так как его заявка на участие на подтверждена"));
    }
}