package io.microconfig.plugin.utils;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.microconfig.plugin.PluginException;

import java.io.File;

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

    public static String fileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0) return name.substring(lastDot);

        throw new PluginException("File " + file + " doesn't have an extension. Unable to resolve component type.");
    }
}