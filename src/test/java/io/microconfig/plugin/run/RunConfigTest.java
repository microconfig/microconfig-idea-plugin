package io.microconfig.plugin.run;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationTypeUtil;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.ExecutionEnvironmentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.PlatformTestUtil;
import io.microconfig.plugin.MicroconfigRepoTestCase;
import org.jdom.Element;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RunConfigTest extends MicroconfigRepoTestCase {
    @Test
    void settingsSurviveSerialization() {
        RunConfig config = newRunConfig();
        Element element = new Element("configuration");
        config.writeExternal(element);

        RunConfig restored = (RunConfig) factory().createTemplateConfiguration(project());
        restored.readExternal(element);

        assertSameSettings(config, restored);
    }

    @Test
    void editorAppliesSettingsToClone() throws Exception {
        RunConfig config = newRunConfig();
        RunConfig clone = (RunConfig) config.clone();
        SettingsEditor<RunConfig> editor = config.getConfigurationEditor();
        try {
            editor.getComponent();
            editor.resetFrom(config);
            clone.setEnv("other");

            editor.applyTo(clone);

            assertSameSettings(config, clone);
            assertNotSame(editor, clone.getConfigurationEditor());
        } finally {
            Disposer.dispose(editor);
        }
    }

    @Test
    void runGeneratesConfigs() throws Exception {
        File destination = new File(projectDir, "build/configs");
        RunnerAndConfigurationSettings settings = RunManager.getInstance(project()).createConfiguration("Generate", factory());
        RunConfig config = (RunConfig) settings.getConfiguration();
        config.setEnv("prod");
        config.setDestination(destination.getAbsolutePath());

        RunContentDescriptor descriptor = run(settings);
        try {
            ProcessHandler process = descriptor.getProcessHandler();
            assertTrue(process.waitFor(120_000), "microconfig didn't finish");
            assertEquals(0, process.getExitCode());

            String payments = Files.readString(new File(destination, "payments/application.yaml").toPath());
            assertTrue(payments.contains("http://localhost:9761"), payments);
        } finally {
            Disposer.dispose(descriptor);
        }
    }

    private RunContentDescriptor run(RunnerAndConfigurationSettings settings) throws Exception {
        ExecutionEnvironment environment = ExecutionEnvironmentBuilder.create(DefaultRunExecutor.getRunExecutorInstance(), settings).build();
        assertInstanceOf(Runner.class, environment.getRunner());

        AtomicReference<RunContentDescriptor> descriptor = new AtomicReference<>();
        environment.setCallback(descriptor::set);
        environment.getRunner().execute(environment);
        PlatformTestUtil.waitWithEventsDispatching("run didn't start", () -> descriptor.get() != null, 60);
        return descriptor.get();
    }

    private RunConfig newRunConfig() {
        RunConfig config = (RunConfig) factory().createTemplateConfiguration(project());
        config.setEnv("prod");
        config.setGroups("main");
        config.setServices("payments");
        config.setDestination("/tmp/configs");
        return config;
    }

    private ConfigurationFactory factory() {
        return ConfigurationTypeUtil.findConfigurationType(RunConfigType.class).getConfigurationFactories()[0];
    }

    private void assertSameSettings(RunConfig expected, RunConfig actual) {
        assertEquals(expected.getEnv(), actual.getEnv());
        assertEquals(expected.getGroups(), actual.getGroups());
        assertEquals(expected.getServices(), actual.getServices());
        assertEquals(expected.getDestination(), actual.getDestination());
    }
}
