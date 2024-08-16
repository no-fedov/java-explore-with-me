package ru.practicum.client;

import ru.practicum.dto.URLParameter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PathBuilder {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Map<String, Object> buildParameters(URLParameter urlParameter) {
        Map<String, Object> parameters = new HashMap<>();

        parameters.put("start",convertTimeToString(urlParameter.getStart()));
        parameters.put("end", convertTimeToString(urlParameter.getEnd()));

        if (urlParameter.getUris() != null) {
            parameters.put("uris", String.join(",", urlParameter.getUris()));
        }

        parameters.put("unique", urlParameter.getUnique());
        return parameters;
    }

    public static String buildPath(Map<String, Object> parameters) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("?");
        for (String key : parameters.keySet()) {
            stringBuilder.append(key).append("={").append(key).append("}").append("&");
        }
        return stringBuilder.substring(0, stringBuilder.capacity() - 1);
    }

    private static String convertTimeToString(LocalDateTime time) {
        return time.format(FORMATTER);
    }
}
