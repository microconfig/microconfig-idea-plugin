package io.microconfig.plugin.utils;

import io.microconfig.plugin.actions.placeholders.PlaceholderBorders;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PlaceholderUtilsTest {
    private static final String LINE = "server.port=${ports@eureka.port}";
    private static final String PLACEHOLDER = "${ports@eureka.port}";

    @Test
    public void should_return_false_if_on_key() {
        assertThat(PlaceholderBorders.borders(LINE, 3).isInsidePlaceholder());
    }

    @Test
    public void should_resolve_substring_if_in_brackets() {
        PlaceholderBorders borders = PlaceholderBorders.borders(LINE, 27);
        assertThat(borders.isInsidePlaceholder()).isTrue();
        assertThat(borders.value()).isEqualTo(PLACEHOLDER);
    }

    @Test
    public void should_handle_middle_placeholder() {
        String line = "${first}${middle}${second}";
        PlaceholderBorders borders = PlaceholderBorders.borders(line, 11);
        assertThat(borders.isInsidePlaceholder()).isTrue();
        assertThat(borders.value()).isEqualTo("${middle}");
    }

    @Test
    public void should_handle_middle_text() {
        String line = "${first}middle${second}";
        assertThat(PlaceholderBorders.borders(line, 11).isInsidePlaceholder()).isFalse();
    }
}