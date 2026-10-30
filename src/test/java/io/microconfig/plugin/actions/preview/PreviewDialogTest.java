package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.testFramework.TestActionEvent;
import io.microconfig.plugin.MicroconfigRepoTestCase;
import io.microconfig.plugin.microconfig.impl.MicroconfigApiImpl;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Arrays;

import static com.intellij.openapi.actionSystem.CommonShortcuts.ESCAPE;
import static com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PreviewDialogTest extends MicroconfigRepoTestCase {
    @Test
    void escapeInPreviewClosesDialog() {
        openWithCaretAfter("components/payments/application.yaml", "eureka:");
        PreviewDialog dialog = new PreviewDialog(editorContext(), new MicroconfigApiImpl());

        AnAction escape = escapeActionOf(dialog.createCenterPanel());
        escape.actionPerformed(TestActionEvent.createTestEvent(escape));

        assertTrue(dialog.isDisposed());
        assertEquals(CANCEL_EXIT_CODE, dialog.getExitCode());
    }

    private AnAction escapeActionOf(JComponent component) {
        return ActionUtil.getActions(component).stream()
                .filter(action -> Arrays.equals(ESCAPE.getShortcuts(), action.getShortcutSet().getShortcuts()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Escape isn't bound in preview"));
    }
}
