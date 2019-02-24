package io.microconfig.plugin.utils;

import org.junit.Test;

import java.util.Optional;

import static io.microconfig.plugin.utils.PlaceholderUtils.insidePlaceholderBrackets;
import static io.microconfig.plugin.utils.PlaceholderUtils.placeholderSubstring;
import static org.fest.assertions.Assertions.assertThat;

public class PlaceholderUtilsTest {
    private static final String LINE = "server.port=${ports@eureka.port}";
    private static final String PLACEHOLDER = "${ports@eureka.port}";

    @Test
    public void should_return_false_if_on_key() {
        assertThat(insidePlaceholderBrackets(LINE, 3)).isFalse();
    }

    @Test
    public void should_resolve_substring_if_in_brackets() {
        Optional<String> result = placeholderSubstring(LINE, 27);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo(PLACEHOLDER);
    }

    @Test
    public void should_handle_middle_placeholder() {
        String line = "${first}${middle}${second}";
        Optional<String> result = placeholderSubstring(line, 11);
        assertThat(result.isPresent()).isTrue();
        assertThat(result.get()).isEqualTo("${middle}");
    }

    @Test
    public void should_handle_middle_text() {
        String line = "${first}middle${second}";
        Optional<String> result = placeholderSubstring(line, 11);
        assertThat(result.isPresent()).isFalse();
    }
}