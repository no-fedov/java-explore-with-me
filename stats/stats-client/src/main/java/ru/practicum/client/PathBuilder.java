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
        StringBuilder path = new StringBuilder();
        path.append("?");
        for (String key : parameters.keySet()) {
            path.append(key).append("={").append(key).append("}").append("&");
        }
        return path.substring(0, path.length() - 1);
    }

    private static String convertTimeToString(LocalDateTime time) {
        return time.format(FORMATTER);
    }
}
