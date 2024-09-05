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
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.CommentService;

import java.time.LocalDateTime;

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
        Event currentEvent = getCurrentEvent(eventId);
        Request currentRequest = requestRepository.findByRequester_IdAndEvent_Id(userId, eventId)
                .orElseThrow(() -> new CommentActionException("Пользователь не может оставить комментарий, " +
                        "так как его заявка на участие на подтверждена"));
        Comment newComment = CommentMapper.convertToCommentFromNewCommentDto(comment);
        newComment.setPublishedOn(LocalDateTime.now());
        newComment.setStatus(CommentStatus.PENDING);
        commentRepository.save(newComment);
        return CommentMapper.convertToCommentDtoFromComment(eventId, currentUser, newComment);
    }

    @Override
    public CommentStatusUpdateResult decisionComments(Long userId,
                                                      Long eventId,
                                                      CommentStatusUpdateRequest commentStatusUpdateRequest) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
        eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() -> new NotFoundException("У пользователя нет такого события"));
        return null;
    }


    private User getCurrentUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Такого пользователя нет"));
    }

    private Event getCurrentEvent(Long eventId) {
       return eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Такого события нет"));
    }

//    private Request getCurrentRequest(Long userId, Long eventId) {
//        requestRepository.getRequestByEvent()
//
//    }
}
