package io.microconfig.plugin;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.junit5.RunInEdt;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Runs a test inside a headless IDE with a project in its own temp dir.
 * Test microconfig repo from resources is copied into the project dir, so plugin works with it as with a real project.
 */
@RunInEdt(writeIntent = true)
public abstract class MicroconfigRepoTestCase {
    protected CodeInsightTestFixture fixture;
    protected File projectDir;
    protected File repoDir;

    @BeforeEach
    void setUpFixture() throws Exception {
        fixture = createFixture();
        fixture.setUp();
        projectDir = new File(project().getBasePath());
        repoDir = new File(projectDir, "repo");
        copyRepo();
    }

    private CodeInsightTestFixture createFixture() {
        IdeaTestFixtureFactory factory = IdeaTestFixtureFactory.getFixtureFactory();
        var projectFixture = factory.createFixtureBuilder(getClass().getSimpleName()).getFixture();
        return factory.createCodeInsightFixture(projectFixture);
    }

    private void copyRepo() throws Exception {
        FileUtil.delete(repoDir);
        FileUtil.copyDir(new File(getClass().getResource("/repo").toURI()), repoDir);
        LocalFileSystem.getInstance().refreshAndFindFileByIoFile(repoDir).refresh(false, true);
    }

    @AfterEach
    void tearDownFixture() throws Exception {
        FileUtil.delete(repoDir);
        fixture.tearDown();
    }

    protected Project project() {
        return fixture.getProject();
    }

    protected File repoFile(String path) {
        return new File(repoDir, path);
    }

    protected void openWithCaretAfter(String path, String textBeforeCaret) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByIoFile(repoFile(path));
        fixture.openFileInEditor(file);
        String text = fixture.getEditor().getDocument().getText();
        int index = text.indexOf(textBeforeCaret);
        assertTrue(index >= 0, "'" + textBeforeCaret + "' not found in " + path);
        fixture.getEditor().getCaretModel().moveToOffset(index + textBeforeCaret.length());
    }

    protected File selectedFile() {
        VirtualFile[] selected = FileEditorManager.getInstance(project()).getSelectedFiles();
        assertEquals(1, selected.length);
        return new File(selected[0].getPath());
    }
}
