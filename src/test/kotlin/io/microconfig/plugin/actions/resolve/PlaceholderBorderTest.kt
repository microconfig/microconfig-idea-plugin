package io.microconfig.plugin.actions.resolve

import io.microconfig.plugin.actions.resolve.PlaceholderBorder.Companion.borders
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PlaceholderBorderTest {
    @Test
    fun textKeyPosition() {
        assertFalse(borders(LINE, 3).isInsidePlaceholder())
        assertFalse(borders(LINE, 10).isInsidePlaceholder())
    }

    @Test
    fun simplePlaceholderBorders() {
        val borders = borders(LINE, 27)
        assertTrue(borders.isInsidePlaceholder())
        assertEquals(PLACEHOLDER, borders.value())
    }

    @Test
    fun compositePlaceholder() {
        val line = "!!!\${c@prop:\${c2@p2:hello}} \${c3@c4}!!!"
        assertEquals("\${c@prop:\${c2@p2:hello}}", borders(line, 9).value())
        assertEquals("\${c2@p2:hello}", borders(line, 16).value())
    }

    @Test
    fun placeholderInTheMiddle() {
        val borders = borders("\${first}\${c@middle}\${second}", 11)
        assertTrue(borders.isInsidePlaceholder())
        assertEquals("\${c@middle}", borders.value())
    }

    @Test
    fun textInTheMiddle() {
        assertFalse(borders("\${first}middle\${second}", 11).isInsidePlaceholder())
    }

    @Test
    fun placeholderInTheBeginning() {
        val line = "ribbon.ReadTimeout=\${this@name} #{40601000}"
        assertNull(borders(line, line.length - 1).value())
    }

    @Test
    fun ignoresUnrelatedTemplateBeforeARealPlaceholder() {
        val line = "\${external} text \${component@key}"

        assertNull(borders(line, 4).value())
        assertNull(borders(line, 13).value())
        assertEquals("\${component@key}", borders(line, line.indexOf("key")).value())
    }

    @Test
    fun keepsExpressionInDefaultValueIntact() {
        val placeholder = "\${component@key:#{1 + 2}}"

        assertEquals(placeholder, borders(placeholder, 5).value())
    }

    @Test
    fun keepsQuotedClosingBraceInsideDefaultValue() {
        val placeholder = "\${component@key:'a}b'}"

        assertEquals(placeholder, borders(placeholder, 5).value())
    }

    @Test
    fun findsOuterPlaceholderAfterNestedDefault() {
        val placeholder = "\${outer@key:\${inner@key}suffix}"

        assertEquals(placeholder, borders(placeholder, placeholder.indexOf("suffix") + 2).value())
    }

    @Test
    fun rejectsUnclosedPlaceholder() {
        assertNull(borders("value=\${component@key", 15).value())
    }

    @Test
    fun distinguishesClosingBoundaryFromFollowingText() {
        val line = "\${component@key} tail"
        val end = line.indexOf('}') + 1

        assertTrue(borders(line, end).isInsidePlaceholder())
        assertFalse(borders(line, end + 1).isInsidePlaceholder())
    }

    companion object {
        private const val LINE = "server.port=\${ports@eureka.port}"
        private const val PLACEHOLDER = "\${ports@eureka.port}"
    }
}
