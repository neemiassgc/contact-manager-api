package spring.manager.api.misc;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class Tools {

    private Tools() {}

    public static <L, R> Map<L, R> immutableMap(final Map<L, R> map) {
        return Objects.isNull(map) ? null : Collections.unmodifiableMap(map);
    }
}
