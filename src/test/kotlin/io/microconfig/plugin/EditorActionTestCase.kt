package io.microconfig.plugin

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.application.ApplicationManager.getApplication
import com.intellij.testFramework.replaceService
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockingDetails

abstract class EditorActionTestCase : MicroconfigRepoTestCase() {
    protected lateinit var hints: HintManager

    @BeforeEach
    fun captureHints() {
        hints = mock(HintManager::class.java)
        getApplication().replaceService(HintManager::class.java, hints, fixture.testRootDisposable)
    }

    protected fun informationHint(): String = hintText("showInformationHint")

    protected fun errorHint(): String = hintText("showErrorHint")

    private fun hintText(method: String): String = mockingDetails(hints).invocations
        .single { it.method.name == method }.arguments[1] as String
}
