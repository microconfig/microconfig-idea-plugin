package io.microconfig.plugin.actions.resolve

import io.microconfig.core.properties.resolvers.placeholder.PlaceholderBorders.Companion.findPlaceholderIn
import kotlin.jvm.optionals.getOrNull

class PlaceholderBorder private constructor(private val value: String?, private val start: Int, private val end: Int) {
    fun isInsidePlaceholder(): Boolean = start >= 0 && start < end

    fun value(): String? = value

    companion object {
        fun borders(line: String, offset: Int): PlaceholderBorder = findAtOffset(line, offset) ?: empty()

        private fun findAtOffset(line: String, offset: Int): PlaceholderBorder? {
            var result: PlaceholderBorder? = null
            var searchFrom = 0
            while (searchFrom <= offset) {
                val candidate = findPlaceholderIn(line, searchFrom).getOrNull() ?: break
                if (candidate.startIndex > offset) break
                if (offset <= candidate.endIndex) {
                    result = PlaceholderBorder(candidate.toString(), candidate.startIndex, candidate.endIndex)
                }
                searchFrom = candidate.startIndex + 2
            }
            return result
        }

        private fun empty(): PlaceholderBorder = PlaceholderBorder(null, -1, -1)
    }
}
