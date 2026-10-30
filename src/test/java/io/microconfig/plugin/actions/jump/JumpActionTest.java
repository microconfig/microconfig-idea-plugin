package io.microconfig.plugin.actions.jump;

import com.intellij.openapi.fileEditor.FileEditorManager;
import io.microconfig.plugin.MicroconfigRepoTestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JumpActionTest extends MicroconfigRepoTestCase {
    private static final String JUMP_ACTION = "Microconfig.JumpToAction";

    @Test
    void jumpFromEnvToComponent() {
        openWithCaretAfter("envs/dev.yaml", "- pay");

        fixture.performEditorAction(JUMP_ACTION);

        assertEquals(repoFile("components/payments/application.yaml"), selectedFile());
    }

    @Test
    void jumpToInclude() {
        openWithCaretAfter("components/payments/application.yaml", "#include eur");

        fixture.performEditorAction(JUMP_ACTION);

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile());
    }

    @Test
    void jumpToPlaceholderSource() {
        openWithCaretAfter("components/payments/application.yaml", "${eureka@server.h");

        fixture.performEditorAction(JUMP_ACTION);

        assertEquals(repoFile("components/eureka/application.yaml"), selectedFile());
        assertEquals(1, selectedCaretLine());
    }

    private int selectedCaretLine() {
        return FileEditorManager.getInstance(project()).getSelectedTextEditor().getCaretModel().getLogicalPosition().line;
    }
}
