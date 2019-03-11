package io.microconfig.plugin.actions.resolve;

import org.junit.Test;

import static io.microconfig.plugin.actions.resolve.PlaceholderBorders.borders;
import static org.fest.assertions.Assertions.assertThat;

public class PlaceholderBordersTest {
    private static final String LINE = "server.port=${ports@eureka.port}";
    private static final String PLACEHOLDER = "${ports@eureka.port}";

    @Test
    public void textKeyPosition() {
        assertThat(borders(LINE, 3).isInsidePlaceholder());
        assertThat(borders(LINE, 10).isInsidePlaceholder());
    }

    @Test
    public void testSimplePlaceholderBorders() {
        PlaceholderBorders borders = borders(LINE, 27);
        assertThat(borders.isInsidePlaceholder()).isTrue();
        assertThat(borders.value()).isEqualTo(PLACEHOLDER);
    }

    @Test
    public void testCompositePlaceholder() {
        String line = "!!!${c@prop:${c2@p2:hello}} ${c3@c4}!!!";
        assertThat(borders(line, 9).value()).isEqualTo("${c@prop:${c2@p2:hello}}");
        assertThat(borders(line, 16).value()).isEqualTo("${c2@p2:hello}");
    }

    @Test
    public void testPlaceholderInTheMiddle() {
        String line = "${first}${c@middle}${second}";
        PlaceholderBorders borders = borders(line, 11);
        assertThat(borders.isInsidePlaceholder()).isTrue();
        assertThat(borders.value()).isEqualTo("${c@middle}");
    }

    @Test
    public void testTextInTheMiddle() {
        assertThat(borders("${first}middle${second}", 11).isInsidePlaceholder()).isFalse();
    }

    @Test
    public void testPlaceholderInTheBeginning() {
        String line = "ribbon.ReadTimeout=${this@name} #{40601000}";
        assertThat(borders(line, line.length() - 1).value()).isEqualTo(null);
    }
}