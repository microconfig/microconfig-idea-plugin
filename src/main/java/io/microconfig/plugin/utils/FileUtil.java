package io.microconfig.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.microconfig.plugin.actions.common.PluginException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.nio.file.Files.walk;
import static java.util.Collections.addAll;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public class FileUtil {
    public static File toFile(VirtualFile virtualFile) {
        return new File(virtualFile.getPath());
    }

    public static VirtualFile toVirtualFile(File file) {
        return LocalFileSystem.getInstance().findFileByIoFile(file);
    }

    public static PsiFile toPsiFile(Project project, VirtualFile virtualFile) {
        return PsiManager.getInstance(project).findFile(virtualFile);
    }

    public static Optional<File> find(File root, Predicate<File> predicate) {
        Deque<File> queue = new ArrayDeque<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            File dir = queue.removeFirst();
            if (predicate.test(dir)) return of(dir);
            File[] child = dir.listFiles(File::isDirectory);
            if (child == null) continue;
            addAll(queue, child);
        }

        return empty();
    }
}