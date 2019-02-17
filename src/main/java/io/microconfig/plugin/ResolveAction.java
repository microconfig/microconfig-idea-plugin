package io.microconfig.plugin;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import static io.microconfig.plugin.ContextUtils.componentType;
import static io.microconfig.plugin.ContextUtils.currentLine;

public class ResolveAction extends AnAction {
    private final FileFinder fileFinder = new FileFinder();
    private final ComponentNameResolver nameResolver = new ComponentNameResolver();

    public void actionPerformed(AnActionEvent event) {
        PluginContext context = new PluginContext(event);
        if (context.notFull()) return;

        try {
            nameResolver.resolve(currentLine(context))
                .flatMap(cn -> fileFinder.resolveComponent(context.project, cn))
                .flatMap(dir -> fileFinder.findComponentFile(context.project, dir, componentType(context)))
                .ifPresent(f -> f.navigate(true));
        } catch (PluginException e) {
            HintManager.getInstance().showErrorHint(context.editor, e.getMessage());
        } catch (NullPointerException ignore) {
        }
    }


}