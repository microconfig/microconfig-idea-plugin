package io.microconfig.plugin;

import java.util.Optional;

public class OptionalUtil {

    public static <T> Optional<T> some(T value) {
        return Optional.of(value);
    }

    public static <T> Optional<T> optional(T value) {
        return Optional.ofNullable(value);
    }

}
