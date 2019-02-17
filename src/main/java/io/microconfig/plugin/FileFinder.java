package io.microconfig.plugin;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Arrays.stream;
import static java.util.Comparator.comparingInt;
import static java.util.Optional.ofNullable;

public class FileFinder {
    public Optional<VirtualFile> resolveComponent(Project project, String component) {
        return ofNullable(findDirectory(project, component));
    }

    private VirtualFile findDirectory(Project project, String dirName) {
        AtomicReference<VirtualFile> ref = new AtomicReference<>();
        ContentIterator fileIterator = contentIterator(dirName, ref);

        ProjectRootManager.getInstance(project).getFileIndex().iterateContent(fileIterator);

        return ref.get();
    }

    public Optional<PsiFile> findComponentFile(Project project, VirtualFile dir, String extension) {
        return stream(dir.getChildren())
                .filter(f -> f.getName().endsWith(extension))
                .min(comparingInt(f -> f.getName().length()))
                .map(PsiManager.getInstance(project)::findFile);
    }

    private ContentIterator contentIterator(String dirName, AtomicReference<VirtualFile> ref) {
        return f -> {
            if (!f.isDirectory() || !f.getName().equals(dirName)) return true;

            ref.set(f);
            return false;
        };
    }
}
