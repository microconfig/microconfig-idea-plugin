package io.microconfig.plugin.actions.preview;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.testFramework.TestActionEvent;
import io.microconfig.plugin.MicroconfigRepoTestCase;
import io.microconfig.plugin.microconfig.ConfigOutput;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import io.microconfig.plugin.microconfig.impl.MicroconfigApiImpl;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static com.intellij.openapi.actionSystem.CommonShortcuts.ESCAPE;
import static com.intellij.openapi.ui.DialogWrapper.CANCEL_EXIT_CODE;
import static io.microconfig.core.properties.ConfigFormat.YAML;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PreviewDialogTest extends MicroconfigRepoTestCase {
    @Test
    void defaultsToFirstAvailableEnvironment() {
        assertInitialEnvironment(List.of("dev", "prod"), Optional.empty(), "dev");
    }

    @Test
    void omitsEmptyEnvironmentFromOptions() {
        assertInitialEnvironment(List.of("", "dev", "prod"), Optional.empty(), "dev");
    }

    @Test
    void usesBlankWhenNoEnvironmentsAreAvailable() {
        assertInitialEnvironment(List.of(), Optional.empty(), "");
    }

    @Test
    void prefersDetectedEnvironment() {
        assertInitialEnvironment(List.of("dev", "prod"), Optional.of("prod"), "prod");
    }

    private void assertInitialEnvironment(List<String> envs, Optional<String> detectedEnv, String expectedEnv) {
        openWithCaretAfter("components/payments/application.yaml", "eureka:");
        var context = editorContext();
        MicroconfigApi api = mock(MicroconfigApi.class);
        when(api.getEnvs(context.projectDir())).thenReturn(new LinkedHashSet<>(envs));
        when(api.detectEnvOr(context.currentFile())).thenReturn(detectedEnv);
        when(api.buildConfigs(context.currentFile(), context.projectDir(), expectedEnv))
                .thenReturn(new ConfigOutput(YAML, "preview"));

        PreviewDialog dialog = new PreviewDialog(context, api);
        try {
            JComboBox<?> comboBox = Arrays.stream(dialog.createNorthPanel().getComponents())
                    .filter(JComboBox.class::isInstance)
                    .map(JComboBox.class::cast)
                    .findFirst().orElseThrow();
            assertEquals(expectedEnv, comboBox.getSelectedItem());
            var expectedOptions = envs.stream().filter(env -> !env.isEmpty()).toList();
            assertEquals(expectedOptions.size(), comboBox.getItemCount());
            for (int i = 0; i < expectedOptions.size(); i++) {
                assertEquals(expectedOptions.get(i), comboBox.getItemAt(i));
            }
            verify(api).buildConfigs(context.currentFile(), context.projectDir(), expectedEnv);
        } finally {
            dialog.close(CANCEL_EXIT_CODE);
        }
    }

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
