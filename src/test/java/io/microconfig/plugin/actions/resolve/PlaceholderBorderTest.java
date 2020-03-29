package io.microconfig.plugin.actions.resolve;

import org.junit.Test;

import static io.microconfig.plugin.actions.resolve.PlaceholderBorder.borders;
import static org.junit.jupiter.api.Assertions.*;

public class PlaceholderBorderTest {
    private static final String LINE = "server.port=${ports@eureka.port}";
    private static final String PLACEHOLDER = "${ports@eureka.port}";

    @Test
    public void textKeyPosition() {
        assertFalse(borders(LINE, 3).isInsidePlaceholder());
        assertFalse(borders(LINE, 10).isInsidePlaceholder());
    }

    @Test
    public void testSimplePlaceholderBorders() {
        PlaceholderBorder borders = borders(LINE, 27);
        assertTrue(borders.isInsidePlaceholder());
        assertEquals(borders.value(), PLACEHOLDER);
    }

    @Test
    public void testCompositePlaceholder() {
        String line = "!!!${c@prop:${c2@p2:hello}} ${c3@c4}!!!";
        assertEquals(borders(line, 9).value(), "${c@prop:${c2@p2:hello}}");
        assertEquals(borders(line, 16).value(), "${c2@p2:hello}");
    }

    @Test
    public void testPlaceholderInTheMiddle() {
        String line = "${first}${c@middle}${second}";
        PlaceholderBorder borders = borders(line, 11);
        assertTrue(borders.isInsidePlaceholder());
        assertEquals(borders.value(), "${c@middle}");
    }

    @Test
    public void testTextInTheMiddle() {
        assertFalse(borders("${first}middle${second}", 11).isInsidePlaceholder());
    }

    @Test
    public void testPlaceholderInTheBeginning() {
        String line = "ribbon.ReadTimeout=${this@name} #{40601000}";
        assertNull(borders(line, line.length() - 1).value());
    }
}