package io.microconfig.plugin.microconfig.impl;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import io.microconfig.io.FsReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static io.microconfig.plugin.utils.FileUtil.toVirtualFile;
import static java.util.stream.Collectors.toList;

public class VirtualFileReader implements FsReader {
    @Override
    public String readFully(File file) {
        Document document = documentOf(file);
        return document != null ? document.getText() : readFromDisk(file);
    }

    private Document documentOf(File file) {
        VirtualFile virtualFile = toVirtualFile(file);
        return virtualFile == null ? null : FileDocumentManager.getInstance().getDocument(virtualFile);
    }

    private String readFromDisk(File file) {
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public List<String> readLines(File file) {
        return lines(file)
            .collect(toList());
    }

    @Override
    public Optional<String> firstLineOf(File file, Predicate<String> predicate) {
        return lines(file)
            .filter(predicate)
            .findFirst();
    }

    private Stream<String> lines(File file) {
        return new BufferedReader(new StringReader(readFully(file))).lines();
    }
}
