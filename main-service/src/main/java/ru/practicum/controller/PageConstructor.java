package ru.practicum.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageConstructor {
    public static Pageable getPage(Integer from, Integer size) {
        return PageRequest.of(from / size, size);
    }
}
