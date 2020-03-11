package io.microconfig.plugin;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
@State(name = "MicroconfigSettings", storages = @Storage("microconfig.xml"), defaultStateAsResource = true)
public class MicroconfigSettings implements PersistentStateComponent<MicroconfigSettings> {

    public String version = "0.0.0";

    @Nullable
    @Override
    public MicroconfigSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MicroconfigSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}

