package ru.practicum.dto.event;

/**
 * Статусы для события, когда админ принимает решение
 */
public enum StateEventAdmin {
    PUBLISH_EVENT,
    CANCEL_REVIEW,
    SEND_TO_REVIEW
}
