package io.microconfig.plugin;

import org.junit.Ignore;
import org.junit.Test;

import static io.microconfig.plugin.utils.PlaceholderUtils.placeholderSubstring;
import static org.fest.assertions.Assertions.assertThat;

public class PlaceholderUtilsTest {
    private static final String LINE = "server.port=${ports@eureka.port}";
    private static final String PLACEHOLDER = "ports@eureka.port";

    @Ignore //todo think about usages
    public void should_resolve_whole_line_if_not_in_brackets() {
        assertThat(placeholderSubstring(LINE, 7)).isSameAs(LINE);
    }

    @Test
    public void should_resolve_substring_if_in_brackets() {
        assertThat(placeholderSubstring(LINE, 27)).isEqualTo(PLACEHOLDER);
    }

    @Test
    public void should_handle_middle_placeholder() {
        String line = "${first}${middle}${second}";
        assertThat(placeholderSubstring(line, 11)).isEqualTo("middle");
    }

    @Test
    public void should_handle_middle_text() {
        String line = "${first}middle${second}";
        assertThat(placeholderSubstring(line, 11)).isEqualTo(line);
    }

}