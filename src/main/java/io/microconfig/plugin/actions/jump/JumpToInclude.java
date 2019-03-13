package io.microconfig.plugin.actions.jump;

import io.microconfig.plugin.actions.common.ActionHandler;
import io.microconfig.plugin.actions.common.PluginContext;
import io.microconfig.plugin.microconfig.MicroconfigApi;
import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public class JumpToInclude implements ActionHandler {
    @Override
    public void onAction(PluginContext context, MicroconfigApi api) {
        File source = api.findIncludeSource(context.currentLine(), context.currentColumn(), context.currentFile(), context.projectDir());
        context.navigateTo(source);
    }
}
