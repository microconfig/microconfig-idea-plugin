package io.microconfig.plugin.microconfig;

import com.intellij.openapi.editor.LogicalPosition;
import io.microconfig.plugin.MicroconfigRepoTestCase;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PluginContextTest extends MicroconfigRepoTestCase {
    private static final String ENV = "envs/dev.yaml";

    @Test
    void tokenUnderCaret() {
        openWithCaretAfter(ENV, "- pay");

        assertEquals("payments", editorContext().currentToken());
    }

    @Test
    void tokenRightAfterCaret() {
        openWithCaretAfter(ENV, "- ");

        assertEquals("eureka", editorContext().currentToken());
    }

    @Test
    void tokenAtLineStart() {
        openWithCaretAt(ENV, 0, 0);

        assertEquals("main", editorContext().currentToken());
    }

    @Test
    void noTokenAtIndentStart() {
        openWithCaretAt(ENV, 3, 0);

        assertEquals("", editorContext().currentToken());
    }

    @Test
    void tokenWithCaretBeyondLineEnd() {
        openWithCaretAt(ENV, 3, 40);

        assertEquals("payments", editorContext().currentToken());
    }

    @Test
    void navigationToMissingFileFails() {
        openWithCaretAt(ENV, 0, 0);
        File missing = repoFile("components/missing/application.yaml");

        var e = assertThrows(IllegalStateException.class, () -> editorContext().navigateTo(missing));

        assertEquals("Can't find file '" + missing + "'", e.getMessage());
    }

    private void openWithCaretAt(String path, int line, int column) {
        openWithCaretAfter(path, "");
        fixture.getEditor().getSettings().setVirtualSpace(true);
        fixture.getEditor().getCaretModel().moveToLogicalPosition(new LogicalPosition(line, column));
    }
}
