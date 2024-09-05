package ru.practicum.repository.custom;

import ru.practicum.model.Request;

import java.util.List;
import java.util.Set;

public interface RequestPrivateRepository {
    /**
     * Получить список запросов на участие в событии
     *
     * @param userId  - идентификатор пользователя
     * @param eventId - идентификатор события к которому нужно найти запросы
     * @return если запрсоов не найдено, возвращается пустой список
     */
    List<Request> getRequestByEvent(Long userId, Long eventId);

    /**
     * Получить список заявок со стасусом "ОЖИДАНИЕ"
     * @param requestIds - множество проверяемых идентификаторов заявок
     * @param eventId - идентификатор события в котором проверяем
     * @return - список запросов со стасусом "ОЖИДАНИЕ"
     */
    List<Request> getPendingRequestsForEvent(Set<Long> requestIds, Long eventId);

    /**
     * Получить количество подтвержденный заявок на учатие в событии
     *
     * @param eventId - идентификатор события
     * @return - количество участников
     */
    Long getCountParticipants(Long eventId);

    /**
     * Узнать количество подтвержденных заявок в перечне заявок для определнного события
     * Если заявка не относится к событию, ничего не происходит.
     * @param requestIds - множество идентификаторов заявок
     * @return - количество подтержденных заявок
     */
    Long getConfirmedCountListRequestOfEvent(Set<Long> requestIds, Long eventId);
}
